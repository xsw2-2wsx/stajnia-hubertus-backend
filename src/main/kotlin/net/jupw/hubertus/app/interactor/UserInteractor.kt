package net.jupw.hubertus.app.interactor

import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.data.repositories.UserRepository
import net.jupw.hubertus.app.entities.User
import net.jupw.hubertus.app.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserInteractor : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun findUserDetailsByUserId(id: Int): User =
        userRepository.findByIdOrNull(id)?.toUser()?: throw UserNotFoundException(id)

    override fun loadUserByUsername(username: String?): User =
        userRepository.findByName(username?: "")?.toUser()?:
        throw UsernameNotFoundException("User with username $username not found")

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