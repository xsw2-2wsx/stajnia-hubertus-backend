package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.AuthenticatedResponse
import net.jupw.hubertus.api.models.AuthenticationRequest
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactor.AuthInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller


@Controller
class AuthController : AuthApi {

    @Autowired
    private lateinit var authInteractor: AuthInteractor

    override fun authPost(req: AuthenticationRequest): ResponseEntity<AuthenticatedResponse> =
        AuthenticatedResponse()
            .token(authInteractor.authenticate(req.username, req.password!!))
            .toResponseEntity()
}