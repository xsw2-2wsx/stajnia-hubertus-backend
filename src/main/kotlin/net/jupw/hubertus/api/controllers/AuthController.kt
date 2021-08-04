package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.AuthenticatedResponse
import net.jupw.hubertus.api.models.AuthenticationRequest
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.AuthInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController


@RestController
class AuthController : AuthApi {

    @Autowired
    private lateinit var authInteractor: AuthInteractor

    override fun auth(authenticationRequest: AuthenticationRequest): ResponseEntity<AuthenticatedResponse> =
        AuthenticatedResponse(authInteractor.authenticate(authenticationRequest.username, authenticationRequest.password))
            .toResponseEntity()
}