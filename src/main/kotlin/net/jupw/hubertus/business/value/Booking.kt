package net.jupw.hubertus.business.value

import net.jupw.hubertus.business.entities.ActivityType
import net.jupw.hubertus.business.entities.BookingOwner
import java.time.LocalDateTime

interface Booking {
    val startTime: LocalDateTime
    val endTime: LocalDateTime
    val activityType: ActivityType
    val subject: String
    val owner: BookingOwner
}
