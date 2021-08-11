package net.jupw.hubertus.app.security

import net.jupw.hubertus.app.interactors.UserInteractor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(private val usersService: UserInteractor, private val tokenService: TokenService) : OncePerRequestFilter() {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        tokenService.getAuthenticatedUserId()?.also { authenticateWithIdFromToken(it, request) }

        filterChain.doFilter(request, response)

    }

    // TODO: handle account deleted or locked
    private fun authenticateWithIdFromToken(userId: Int, request: HttpServletRequest) = try {

        val userDetails = usersService.findUserById(userId)

        if(!userDetails.isEnabled)
            throw DisabledException("User ${userDetails.username} failed token authentication due to account being disabled")
        if(!userDetails.isAccountNonLocked)
            throw LockedException("User ${userDetails.username} failed token authentication due to account being locked")

        val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        auth.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = auth
    }
    catch (e: LockedException) {
        log.debug(e.message)
    }
    catch (e: DisabledException) {
        log.debug(e.message)
    }


}