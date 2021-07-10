package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.User
import net.jupw.hubertus.app.interactors.UserInteractor
import net.jupw.hubertus.api.converters.convertToModel
import net.jupw.hubertus.api.models.UserPassword
import net.jupw.hubertus.api.toResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UsersController : UsersApi {

    @Autowired
    private lateinit var userInteractor: UserInteractor

    override fun usersDelete(userId: Int): ResponseEntity<Unit> {
        userInteractor.deleteUser(userId)
        return ResponseEntity.ok().build()
    }


    override fun usersGet(): ResponseEntity<List<User>> =
        userInteractor.findAllUsers().map { it.convertToModel() }
            .toResponseEntity()

    override fun usersPost(user: User): ResponseEntity<UserPassword> =
        UserPassword(userInteractor.createUser(user.name)).toResponseEntity()

    override fun usersPut(user: User): ResponseEntity<Unit> {
        userInteractor.modifyUser(user.id, user.name, user.email, user.phone, user.locked)
        return ResponseEntity.ok().build()
    }

    override fun usersUserIdResetpasswordPatch(userId: Int): ResponseEntity<UserPassword> =
        UserPassword(userInteractor.resetPassword(userId)).toResponseEntity()

    override fun usersUserIdGet(userId: Int): ResponseEntity<User> =
        userInteractor.findUserById(userId)
            .convertToModel()
            .toResponseEntity()
}