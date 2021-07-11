package net.jupw.hubertus.mocks.business.value

import net.jupw.hubertus.business.entities.ActivityType
import net.jupw.hubertus.business.entities.BookingOwner
import net.jupw.hubertus.business.value.Booking
import net.jupw.hubertus.mocks.business.entities.BookingOwnerMock
import java.time.LocalDateTime

fun bookingMock(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    activity: ActivityType
): Booking = object : Booking {

    override val startTime: LocalDateTime = startTime

    override val endTime: LocalDateTime = endTime

    override val activityType: ActivityType = activity

    override val subject: String = "Test subject"

    override val owner: BookingOwner = BookingOwnerMock()
}
