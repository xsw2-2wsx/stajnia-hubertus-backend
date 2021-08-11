package net.jupw.hubertus.app.security

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtConf {
    companion object {
        private val log: Logger = LogManager.getLogger()

        const val DEFAULT_VALIDITY: Long = 900000
        const val DEFAULT_PASSWD_RECOVERY_VALIDITY = 180000
    }

    @Value("\${api.auth.token.secret}")
    private lateinit var secretValue: String

    private var _key: SecretKey? = null

    val key: SecretKey
        get() {
            if(_key == null) initKey()
            return _key!!
        }

    @Value("\${api.auth.token.issuer}")
    lateinit var issuer: String

    @Value("\${api.auth.token.audience}")
    lateinit var audience: String

    @Value("\${api.auth.token.validity}")
    private lateinit var validityValue: String

    private var _validity: Long? = null

    val validity: Long
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
        } catch (e: Exception) {
            log.warn("Invalid validity time: $validityValue, using the default")
        }

        _validity = DEFAULT_VALIDITY
    }
}