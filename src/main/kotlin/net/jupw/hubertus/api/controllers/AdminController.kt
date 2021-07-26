package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.IdHolder
import net.jupw.hubertus.api.models.Role
import net.jupw.hubertus.api.models.User
import net.jupw.hubertus.api.models.UserPassword
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.BookingInteractor
import net.jupw.hubertus.app.interactors.UserInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

class AdminController : AdminApi {

    @Autowired
    private lateinit var userInteractor: UserInteractor

    @Autowired
    private lateinit var bookingInteractor: BookingInteractor

    override fun adminUsersPost(user: User): ResponseEntity<UserPassword> =
        UserPassword(userInteractor.createUser(user.name)).toResponseEntity()

    override fun adminUsersPut(user: User): ResponseEntity<Unit> =
        userInteractor.modifyUser(user.id, user.name, user.email, user.phone, user.locked)
            .toResponseEntity()

    override fun adminUsersUserIdResetpasswordPatch(userId: Int): ResponseEntity<UserPassword> =
        UserPassword(userInteractor.resetPassword(userId)).toResponseEntity()

    override fun adminUsersUserIdDelete(userId: Int): ResponseEntity<Unit> =
        userInteractor.deleteUser(userId).toResponseEntity()

    override fun adminBookingsBookingIdDelete(bookingId: Int): ResponseEntity<Unit> =
        bookingInteractor.deleteBooking(bookingId).toResponseEntity()

    override fun adminUsersUserIdRolesGet(userId: Int): ResponseEntity<List<Role>> =
        userInteractor.getUserRoles(userId).map { it.toModel() }.toResponseEntity()

    override fun adminUsersUserIdRolesPost(userId: Int, idHolder: IdHolder): ResponseEntity<Unit> =
        userInteractor.addRole(userId, idHolder.id).toResponseEntity()

    override fun adminUsersUserIdRolesRoleIdDelete(userId: Int, roleId: Int): ResponseEntity<Unit> =
        userInteractor.removeRole(userId, roleId).toResponseEntity()

    override fun adminUsersUserIdRolesRoleIdGet(userId: Int, roleId: Int): ResponseEntity<Role> =
        userInteractor.getUserRole(userId, roleId).toModel().toResponseEntity()
}