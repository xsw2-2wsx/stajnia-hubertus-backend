package net.jupw.hubertus.app.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

interface AppUserDetailsService : UserDetailsService {

    fun findUserDetailsByUserId(id: Int): UserDetails

}