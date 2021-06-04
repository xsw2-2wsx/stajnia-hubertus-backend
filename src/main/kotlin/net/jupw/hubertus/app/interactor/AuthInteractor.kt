package net.jupw.hubertus.app.interactor

import io.jsonwebtoken.Jwts
import net.jupw.hubertus.app.entities.User
import net.jupw.hubertus.app.security.JWTConfiguration
import net.jupw.hubertus.app.security.configure
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.*
import org.springframework.stereotype.Service

@Service
class AuthInteractor {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var authManager: AuthenticationManager

    @Autowired
    private lateinit var jwtConf: JWTConfiguration

    fun authenticate(username: String, password: String): String = try {
        val auth = authManager.authenticate(UsernamePasswordAuthenticationToken(username, password))

        Jwts
            .builder()
            .configure(jwtConf)
            .setSubject((auth.principal as User).id.toString())
            .compact()
    }
    catch (e: DisabledException) {
        log.debug("User with username $username failed to obtain a token due to account being disabled")
        throw e
    }
    catch (e: LockedException) {
        log.debug("User with username $username failed to obtain a token due to account being locked")
        throw e
    }
    catch (e: BadCredentialsException) {
        log.debug("Attempt of obtaining token with invalid credentials has been made for username $username")
        throw e
    }
}