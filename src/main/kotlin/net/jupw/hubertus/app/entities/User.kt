package net.jupw.hubertus.app.entities

import net.jupw.hubertus.business.entities.BookingOwner
import org.springframework.security.core.userdetails.UserDetails

interface User : UserDetails, BookingOwner {
    var id: Int

    var name: String

    var passwd: String

    var phone: String?

    var email: String?

    var isLocked: Boolean

    var authorities: List<String>
}