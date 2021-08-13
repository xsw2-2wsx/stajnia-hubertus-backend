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
    }

    enum class Claims(val value: String) {
        PASSWORD_RECOVERY("pwr")
    }

    @Autowired
    private lateinit var conf: JwtConf

    private val currentRequest: HttpServletRequest?
        get() = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request

    fun createAuthenticationToken(userId: Int): String =
        Jwts
            .builder()
            .configure(conf.validity)
            .setSubject(userId.toString())
            .compact()

    fun createPasswordRecoveryAuthToken(userId: Int): String =
        Jwts
            .builder()
            .configure(conf.passwdRecoveryValidity)
            .setSubject(userId.toString())
            .claim(Claims.PASSWORD_RECOVERY.value, true)
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

    fun isPasswordRecoveryToken(): Boolean {
        val token = extractTokenFromRequest(currentRequest!!)?: return false

        return Jwts
            .parserBuilder()
            .configure()
            .build()
            .parseClaimsJws(token)
            .body
            .get(Claims.PASSWORD_RECOVERY.value, Boolean::class.javaObjectType)
            ?: false
    }



    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val header: String = request.getHeader(HttpHeaders.AUTHORIZATION)?: return null

        return if(header.startsWith("Bearer ")) header.substring(7, header.length) else null
    }

    private fun JwtBuilder.configure(validity: Long): JwtBuilder {
        setIssuer(conf.issuer)
        setAudience(conf.audience)
        setExpiration(Date(Date().time + validity))
        signWith(conf.key)
        return this
    }

    private fun JwtParserBuilder.configure(): JwtParserBuilder {
        requireIssuer(conf.issuer)
        requireAudience(conf.audience)
        setSigningKey(conf.key)
        return this
    }


}