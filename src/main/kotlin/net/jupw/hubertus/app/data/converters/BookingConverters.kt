package net.jupw.hubertus.app.data.converters

import net.jupw.hubertus.app.data.entities.BookingEntity
import net.jupw.hubertus.app.entities.BookingImpl

fun BookingEntity.toBooking() = BookingImpl(
    id,
    creationTime,
    startTime,
    endTime,
    activity.toActivity(),
    subject,
    user.toUser(),
)