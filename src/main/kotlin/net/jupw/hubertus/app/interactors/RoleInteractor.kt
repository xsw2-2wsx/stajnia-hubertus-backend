package net.jupw.hubertus.app.interactors

import net.jupw.hubertus.app.data.entities.RoleEntity
import net.jupw.hubertus.app.data.repositories.RoleRepository
import net.jupw.hubertus.app.entities.Authority
import net.jupw.hubertus.app.entities.Role
import net.jupw.hubertus.app.entities.RoleImpl
import net.jupw.hubertus.app.exceptions.RoleDoesNotExistException
import net.jupw.hubertus.app.security.Authorities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class RoleInteractor {

    @Autowired
    private lateinit var roleRepository: RoleRepository

    fun findRoles(): List<Role> = roleRepository.findAll().map { entity -> RoleImpl(
        entity.id, entity.name, entity.description, entity.authorities.map { Authorities.readAuthority(it) }.toSet()
    ) }

    fun findRoleById(id: Int) = roleRepository.findByIdOrNull(id)?.run { RoleImpl(
        id, name, description, authorities.map { Authorities.readAuthority(it) }.toSet()
    ) }?: throw RoleDoesNotExistException(id)

    fun createRole(name: String, description: String, authorities: Set<Authority>) =
        roleRepository.save(RoleEntity(0, name, description, authorities.map { it.key }))

    fun deleteRole(id: Int) = roleRepository.deleteById(id)

    fun getRoleAuthorities(roleId: Int) = findRoleById(roleId).authorities

    @Transactional
    fun setRoleAuthorities(roleId: Int, authorities: Set<Authority>) {
        findRoleEntity(roleId).authorities = authorities.map { it.key }
    }

    @Transactional
    fun removeRoleAuthorities(roleId: Int) {
        findRoleEntity(roleId).authorities = emptyList()
    }

    fun findRoleEntity(id: Int) = roleRepository.findByIdOrNull(id)?: throw RoleDoesNotExistException(id)


}