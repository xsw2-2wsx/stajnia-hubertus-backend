package net.jupw.hubertus.app.interactors

import net.jupw.hubertus.app.data.converters.toUser
import net.jupw.hubertus.app.data.entities.RoleEntity
import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.data.repositories.UserRepository
import net.jupw.hubertus.app.entities.Role
import net.jupw.hubertus.app.entities.RoleImpl
import net.jupw.hubertus.app.entities.User
import net.jupw.hubertus.app.entities.UserImpl
import net.jupw.hubertus.app.exceptions.UserAlreadyExistException
import net.jupw.hubertus.app.exceptions.UserDoesNotHaveSpecifiedRoleException
import net.jupw.hubertus.app.exceptions.UserNotFoundException
import net.jupw.hubertus.app.security.Authorities
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*
import java.util.Collections.emptyList
import javax.transaction.Transactional

@Service
class UserInteractor : UserDetailsService {

    companion object {
        const val GENERATED_PASSWD_LEN = 9
        private val log: Logger = LogManager.getLogger()
    }

    private val random = SecureRandom()

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var roleInteractor: RoleInteractor
    
    val authenticatedUser: User
        get() = SecurityContextHolder.getContext().authentication.principal as User

    fun findAllUsers(): List<User> = userRepository.findAll().map { it.toUser() }

    fun findUserById(id: Int): User =
        userRepository.findByIdOrNull(id)?.toUser()?: throw UserNotFoundException(id)

    fun userExists(username: String): Boolean = Optional.ofNullable(userRepository.findByName(username)).isPresent

    override fun loadUserByUsername(username: String?): User =
        userRepository.findByName(username?: "")?.toUser()?:
        throw UsernameNotFoundException("User with username $username not found")

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    fun createUser(username: String): String {
        if(userExists(username)) throw UserAlreadyExistException(username)

        val password = secRandomString(GENERATED_PASSWD_LEN)

        userRepository.save(UserEntity(
            0, username, passwordEncoder.encode(password), null, null, false, mutableSetOf()
        ))

        return password
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    @Transactional
    fun modifyUser(id: Int, name: String, email: String?, phone: String?, isLocked: Boolean) {
        val userToEdit = findUserEntity(id)
        val userWithUsername = userRepository.findByName(name)

        if(userWithUsername != null && userWithUsername.id != userToEdit.id)
            throw UserAlreadyExistException(name)

        userToEdit.let {
            it.name = name
            it.email = email
            it.phone = phone
            it.isLocked = isLocked
        }

        log.info("User ${userToEdit.name} (id ${userToEdit.id}) has been modified by ${authenticatedUser.name}")
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    fun deleteUser(id: Int) {
        userRepository.deleteById(id)
        log.info("User with id $id has been deleted by user with username ${ authenticatedUser.name }")
    }

    @Transactional
    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).RESET_ANY_PASSWORD)")
    fun resetPassword(id: Int): String {
        val user = findUserEntity(id)
        val newPassword = secRandomString(GENERATED_PASSWD_LEN)
        user.password = passwordEncoder.encode(newPassword)
        log.info("Password of user ${ user.name } has been reset by ${ authenticatedUser.name }")
        return newPassword
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    @Transactional
    fun addRole(userId: Int, roleId: Int) {
        val role = roleInteractor.findRoleEntity(roleId)
        val user = findUserEntity(userId)

        user.roles.add(role)
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    @Transactional
    fun removeRole(userId: Int, roleId: Int) {
        val user = findUserEntity(userId)
        user.roles.removeIf { it.id == roleId }
    }

    fun getUserRole(userId: Int, roleId: Int): Role =
        findUserEntity(userId)
            .roles
            .find { it.id == roleId }
            ?.toRole()
            ?: throw UserDoesNotHaveSpecifiedRoleException(userId, roleId)

    private fun findUserEntity(id: Int) = userRepository.findByIdOrNull(id)?: throw UserNotFoundException(id)

    private fun secRandomString(len: Int) =
        Base64.getEncoder().encodeToString(ByteArray(len).also { random.nextBytes(it) })

    fun getUserRoles(userId: Int): Set<Role> =
        findUserEntity(userId).roles.map { it.toRole() }.toSet()

    fun RoleEntity.toRole() = RoleImpl(id, name, description, authorities.map { Authorities.readAuthority(it) }.toSet())

}