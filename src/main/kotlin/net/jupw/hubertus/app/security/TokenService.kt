package net.jupw.hubertus.app.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.security.SignatureException
import java.util.*
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest

@Service
class TokenService {

    companion object {
        private val log: Logger = LogManager.getLogger()
        const val DEFAULT_VALIDITY: Long = 900000
    }

    enum class Claims(val value: String) {
        PASSWORD_RECOVERY("pwr")
    }

    @Value("\${api.auth.token.secret}")
    private lateinit var secretValue: String

    private var _key: SecretKey? = null

    private val key: SecretKey
        get() {
            if(_key == null) initKey()
            return _key!!
        }

    @Value("\${api.auth.token.issuer}")
    private lateinit var issuer: String

    @Value("\${api.auth.token.audience}")
    private lateinit var audience: String

    @Value("\${api.auth.token.validity}")
    private lateinit var validityValue: String

    private var _validity: Long? = null

    private val validity: Long
        get() {
            if(_validity == null) initValidity()
            return _validity!!
        }

    private fun initKey() =
        if (secretValue.isEmpty()) _key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        else _key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretValue))

    private fun initValidity() {
        if(validityValue.isNotEmpty()) try {
            _validity = validityValue.toLong()
            return
        } catch (ignored: Exception) {}

        _validity = DEFAULT_VALIDITY
    }

    private val currentRequest: HttpServletRequest?
        get() = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request

    fun createAuthenticationToken(userId: Int): String =
        Jwts
            .builder()
            .configure(validity)
            .setSubject(userId.toString())
            .compact()

    fun getAuthenticatedUserId(): Int? {
        val token = extractTokenFromRequest(currentRequest!!)?: return null
        try {
            return Jwts
                .parserBuilder()
                .configure()
                .build()
                .parseClaimsJws(token)
                .body
                .subject
                .toInt()

        } catch (e: SignatureException) {
            log.warn("Token authentication failed due invalid signature")
        } catch (e: ExpiredJwtException) {
            log.debug("Token authentication failed due to expired token")
        } catch (e: JwtException) {
            log.debug("Token authentication failed due to exception: $e")
        }
        return null
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val header: String = request.getHeader(HttpHeaders.AUTHORIZATION)?: return null

        return if(header.startsWith("Bearer ")) header.substring(7, header.length) else null
    }

    private fun JwtBuilder.configure(validity: Long): JwtBuilder {
        setIssuer(issuer)
        setAudience(audience)
        setExpiration(Date(Date().time + validity))
        signWith(key)
        return this
    }

    private fun JwtParserBuilder.configure(): JwtParserBuilder {
        requireIssuer(issuer)
        requireAudience(audience)
        setSigningKey(key)
        return this
    }


}