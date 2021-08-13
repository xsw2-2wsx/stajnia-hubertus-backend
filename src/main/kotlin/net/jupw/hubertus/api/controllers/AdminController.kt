package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.*
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.BookingInteractor
import net.jupw.hubertus.app.interactors.UserInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController : AdminApi {

    @Autowired
    private lateinit var userInteractor: UserInteractor

    @Autowired
    private lateinit var bookingInteractor: BookingInteractor

    override fun createUser(user: User): ResponseEntity<Password> =
        Password(userInteractor.createUser(user.name)).toResponseEntity()

    override fun modifyUser(user: User): ResponseEntity<Unit> =
        userInteractor.modifyUser(user.id, user.name, user.email, user.phone, user.locked)
            .toResponseEntity()

    override fun resetPasswordByUserId(userId: Int): ResponseEntity<Password> =
        Password(userInteractor.resetPassword(userId)).toResponseEntity()

    override fun deleteUserById(userId: Int): ResponseEntity<Unit> =
        userInteractor.deleteUser(userId).toResponseEntity()

    override fun deleteBookingById(bookingId: Int): ResponseEntity<Unit> =
        bookingInteractor.deleteBooking(bookingId).toResponseEntity()

    override fun getUserRoles(userId: Int): ResponseEntity<List<Role>> =
        userInteractor.getUserRoles(userId).map { it.toModel() }.toResponseEntity()

    override fun setUserRoles(userId: Int, idHolder: IdHolder): ResponseEntity<Unit> =
        userInteractor.addRole(userId, idHolder.id).toResponseEntity()

    override fun deleteUserRoleById(userId: Int, roleId: Int): ResponseEntity<Unit> =
        userInteractor.removeRole(userId, roleId).toResponseEntity()

    override fun getUserRoleById(userId: Int, roleId: Int): ResponseEntity<Role> =
        userInteractor.getUserRole(userId, roleId).toModel().toResponseEntity()
}