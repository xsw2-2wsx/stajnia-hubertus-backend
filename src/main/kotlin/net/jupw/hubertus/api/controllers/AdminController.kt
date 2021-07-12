package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.User
import net.jupw.hubertus.api.models.UserPassword
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.UserInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity

class AdminController : AdminApi {

    @Autowired
    private lateinit var userInteractor: UserInteractor

    override fun adminUsersPost(user: User): ResponseEntity<UserPassword> =
        UserPassword(userInteractor.createUser(user.name)).toResponseEntity()

    override fun adminUsersPut(user: User): ResponseEntity<Unit> {
        userInteractor.modifyUser(user.id, user.name, user.email, user.phone, user.locked)
        return ResponseEntity.ok().build()
    }

    override fun adminUsersUserIdResetpasswordPatch(userId: Int): ResponseEntity<UserPassword> =
        UserPassword(userInteractor.resetPassword(userId)).toResponseEntity()

    override fun adminUsersUserIdDelete(userId: Int): ResponseEntity<Unit> {
        userInteractor.deleteUser(userId)
        return ResponseEntity.ok().build()
    }

}