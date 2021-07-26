package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.Authority
import net.jupw.hubertus.api.models.Role
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.exceptions.AuthorityDoesNotExistException
import net.jupw.hubertus.app.interactors.RoleInteractor
import net.jupw.hubertus.app.security.Authorities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RolesController : RolesApi {

    @Autowired
    private lateinit var roleInteractor: RoleInteractor

    override fun rolesGet(): ResponseEntity<List<Role>> =
        roleInteractor.findRoles().map { it.toModel() }.toResponseEntity()

    override fun rolesPost(role: Role): ResponseEntity<Unit> {
        roleInteractor.createRole(role.name, role.description, emptySet())
        return ResponseEntity.ok().build()
    }


    override fun rolesRoleIdAuthoritiesDelete(roleId: Int): ResponseEntity<Unit> =
        roleInteractor.removeRoleAuthorities(roleId).toResponseEntity()

    override fun rolesRoleIdAuthoritiesGet(roleId: Int): ResponseEntity<List<Authority>> =
        roleInteractor.getRoleAuthorities(roleId).map { it.toModel() }.toResponseEntity()

    override fun rolesRoleIdAuthoritiesPost(roleId: Int, requestBody: List<String>): ResponseEntity<Unit> {
        val authorities = requestBody.map {
            Authorities.valueOfOrNull(it)?: throw AuthorityDoesNotExistException(it)
        }.toSet()

        roleInteractor.setRoleAuthorities(roleId, authorities)

        return ResponseEntity.ok().build()
    }



    override fun rolesRoleIdDelete(roleId: Int): ResponseEntity<Unit> =
        roleInteractor.deleteRole(roleId).toResponseEntity()

    override fun rolesRoleIdGet(roleId: Int): ResponseEntity<Role> =
        roleInteractor.findRoleById(roleId).toModel().toResponseEntity()
}