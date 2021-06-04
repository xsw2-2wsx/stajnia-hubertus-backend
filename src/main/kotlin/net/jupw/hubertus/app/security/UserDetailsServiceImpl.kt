package net.jupw.hubertus.app.security

import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.data.repositories.UserRepository
import net.jupw.hubertus.app.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : AppUserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun findUserDetailsByUserId(id: Int) =
        userRepository.findByIdOrNull(id)?.toUserDetails()?: throw UserNotFoundException(id)

    override fun loadUserByUsername(username: String?): UserDetails =
        userRepository.findByName(username?: "")?.toUserDetails()?: throw UserNotFoundException(username?: "")


    private class UserDetailsImpl(private val user: UserEntity) : UserDetails {
        override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
            user.roles.flatMap { authorities }.toMutableList()

        override fun getPassword(): String = user.password

        override fun getUsername(): String = user.name

        override fun isAccountNonExpired(): Boolean = true

        override fun isAccountNonLocked(): Boolean = !user.isLocked

        override fun isCredentialsNonExpired(): Boolean = true

        override fun isEnabled(): Boolean = true
    }

    fun UserEntity.toUserDetails(): UserDetails = UserDetailsImpl(this)
}