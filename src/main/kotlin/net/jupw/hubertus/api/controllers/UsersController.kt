package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.User
import net.jupw.hubertus.app.interactor.UserInteractor
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

    override fun usersDelete(userId: Int): ResponseEntity<Void> {
        userInteractor.deleteUser(userId)
        return ResponseEntity.ok().build()
    }


    override fun usersGet(): ResponseEntity<MutableList<User>> =
        userInteractor.findAllUsers().map { it.convertToModel() }
            .toMutableList()
            .toResponseEntity()

    override fun usersPost(body: User): ResponseEntity<UserPassword> = UserPassword().apply {
        newPassword = userInteractor.createUser(body.name)
    }.toResponseEntity()

    override fun usersPut(u: User): ResponseEntity<Void> {
        userInteractor.modifyUser(u.id, u.name, u.email, u.phone, u.isLocked)
        return ResponseEntity.ok().build()
    }

    override fun usersUserIdResetpasswordPatch(userId: Int): ResponseEntity<UserPassword> = UserPassword().apply {
        newPassword = userInteractor.resetPassword(userId)
    }.toResponseEntity()
}