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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.IllegalStateException
import java.security.SecureRandom
import java.util.*

@Service
class UserInteractor : UserDetailsService {

    companion object {
        const val GENERATED_PASSWD_LEN = 9
        private val log: Logger = LogManager.getLogger()
    }

    private val random = SecureRandom()

    @Autowired
    private lateinit var userRepository: UserRepository

    fun findUserById(id: Int): User =
        userRepository.findByIdOrNull(id)?.toUser()?: throw UserNotFoundException(id)

    fun userExists(username: String): Boolean = Optional.ofNullable(userRepository.findByName(username)).isPresent

    override fun loadUserByUsername(username: String?): User =
        userRepository.findByName(username?: "")?.toUser()?:
        throw UsernameNotFoundException("User with username $username not found")

    fun createUser(username: String): User {
        if(userExists(username)) throw UserAlreadyExistException(username)

        userRepository.save(UserEntity(
            0, username, secRandomString(GENERATED_PASSWD_LEN), null, null, false, emptyList()
        ))

        return try {
            loadUserByUsername(username)
        } catch (e: UsernameNotFoundException) {
            throw IllegalStateException("Created user does not exist", e)
        }
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