package net.jupw.hubertus.mocks.business.value

import net.jupw.hubertus.business.entities.ActivityType
import net.jupw.hubertus.business.value.Booking
import net.jupw.hubertus.mocks.business.entities.BookingOwnerMock
import java.time.LocalDateTime

fun bookingMock(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    activity: ActivityType
): Booking = Booking(
    startTime,
    endTime,
    activity,
    "Test subject",
    BookingOwnerMock(),
)
