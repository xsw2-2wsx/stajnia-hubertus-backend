package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.User
import net.jupw.hubertus.app.interactors.UserInteractor
import net.jupw.hubertus.api.converters.convertToModel
import net.jupw.hubertus.api.models.UserPassword
import net.jupw.hubertus.api.toResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UsersController : UsersApi {

    @Autowired
    private lateinit var userInteractor: UserInteractor

    override fun getUsers(): ResponseEntity<List<User>> =
        userInteractor.findAllUsers().map { it.convertToModel() }
            .toResponseEntity()

    override fun getUserById(userId: Int): ResponseEntity<User> =
        userInteractor.findUserById(userId)
            .convertToModel()
            .toResponseEntity()

    override fun getUserProfilePicture(userId: Int): ResponseEntity<Resource> =
        userInteractor.getProfilePicture(userId).toResponseEntity()
}