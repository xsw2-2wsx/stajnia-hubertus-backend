package net.jupw.hubertus.app.interactor

import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.data.repositories.UserRepository
import net.jupw.hubertus.app.entities.User
import net.jupw.hubertus.app.exceptions.UserAlreadyExistException
import net.jupw.hubertus.app.exceptions.UserNotFoundException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*
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
    
    val authenticatedUser: User
        get() = SecurityContextHolder.getContext().authentication.principal as User

    fun findAllUsers(): List<User> = userRepository.findAll().map { it.toUser() }

    fun findUserById(id: Int): User =
        userRepository.findByIdOrNull(id)?.toUser()?: throw UserNotFoundException(id)

    fun userExists(username: String): Boolean = Optional.ofNullable(userRepository.findByName(username)).isPresent

    override fun loadUserByUsername(username: String?): User =
        userRepository.findByName(username?: "")?.toUser()?:
        throw UsernameNotFoundException("User with username $username not found")

    fun createUser(username: String): String {
        if(userExists(username)) throw UserAlreadyExistException(username)

        val password = secRandomString(GENERATED_PASSWD_LEN)

        userRepository.save(UserEntity(
            0, username, passwordEncoder.encode(password), null, null, false, emptyList()
        ))

        return password
    }

    @Transactional
    fun modifyUser(id: Int, name: String, email: String?, phone: String?, isLocked: Boolean) {
        val userToEdit = userRepository.findByIdOrNull(id)?: throw UserNotFoundException(id)
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

    fun deleteUser(id: Int) {
        userRepository.deleteById(id)
        log.info("User with id $id has been deleted by user with username ${ authenticatedUser.name }")
    }

    @Transactional
    fun resetPassword(id: Int): String {
        val user = userRepository.findByIdOrNull(id)?: throw UserNotFoundException(id)
        val newPassword = secRandomString(GENERATED_PASSWD_LEN)
        user.password = passwordEncoder.encode(newPassword)
        log.info("Password of user ${ user.name } has been reset by ${ authenticatedUser.name }")
        return newPassword
    }

    private fun secRandomString(len: Int) =
        Base64.getEncoder().encodeToString(ByteArray(len).also { random.nextBytes(it) })

    private fun UserEntity.toUser() = UserImpl(
        id,
        name,
        password,
        phone,
        email,
        isLocked,
        roles.flatMap { it.authorities }
    )

    private class UserImpl(
        override var id: Int,
        override var name: String,
        override var passwd: String,
        override var phone: String?,
        override var email: String?,
        override var isLocked: Boolean,
        override var authorities: List<String>,
    ) : User {
        override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
            authorities.map { GrantedAuthority { it } }.toMutableList()

        override fun getUsername(): String = name

        override fun getPassword(): String = passwd

        override fun isAccountNonLocked(): Boolean = !isLocked

        override fun isAccountNonExpired(): Boolean = true

        override fun isCredentialsNonExpired(): Boolean = true

        override fun isEnabled(): Boolean = true

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UserImpl

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id
        }


    }
}