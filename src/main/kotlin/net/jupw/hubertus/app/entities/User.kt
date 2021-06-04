package net.jupw.hubertus.app.entities

import org.springframework.security.core.userdetails.UserDetails

interface User : UserDetails {
    var id: Int

    var name: String

    var phone: String

    var email: String

    var isLocked: Boolean

    var authorities: List<String>
}