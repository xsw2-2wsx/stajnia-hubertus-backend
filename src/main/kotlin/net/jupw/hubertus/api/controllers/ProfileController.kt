package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.ChangePasswordRequest
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.UserInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController : ProfileApi {

    @Autowired
    private lateinit var userInteractor: UserInteractor

    override fun setProfilePicture(body: Resource): ResponseEntity<Unit> =
        userInteractor.setProfilePicture(body).toResponseEntity()

    override fun deleteProfilePicture(): ResponseEntity<Unit> =
        userInteractor.deleteProfilePicture().toResponseEntity()

    override fun changePassword(changePasswordRequest: ChangePasswordRequest): ResponseEntity<Unit> =
        userInteractor.changePassword(changePasswordRequest.oldPassword, changePasswordRequest.newPassword)
            .toResponseEntity()
}