package net.jupw.hubertus.app.entities

import org.springframework.security.core.GrantedAuthority

class UserImpl(
    override var id: Int,
    override var name: String,
    override var passwd: String,
    override var phone: String?,
    override var email: String?,
    override var isLocked: Boolean,
    override var authorities: List<String>,
    override var profilePicturePath: String?,
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