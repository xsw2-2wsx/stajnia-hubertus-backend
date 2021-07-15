package net.jupw.hubertus.app.entities

import net.jupw.hubertus.business.entities.ActivityType
import net.jupw.hubertus.business.entities.BookingOwner
import java.time.LocalDateTime

class BookingImpl(
    override var id: Int,
    override var creationTime: LocalDateTime,
    override var startTime: LocalDateTime,
    override var endTime: LocalDateTime,
    override var activity: Activity,
    override var subject: String,
    override var user: User,
) : Booking {
    override val activityType: ActivityType = activity

    override val owner: BookingOwner = user
}
