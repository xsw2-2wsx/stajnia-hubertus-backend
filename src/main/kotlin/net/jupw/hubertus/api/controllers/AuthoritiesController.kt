package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.Authority
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.RoleInteractor
import net.jupw.hubertus.app.security.Authorities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthoritiesController : AuthoritiesApi {

    override fun authoritiesGet(): ResponseEntity<List<Authority>> =
        Authorities.values().map { it.toModel() }.toResponseEntity()

}