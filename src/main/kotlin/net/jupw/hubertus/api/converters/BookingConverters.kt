package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.Booking

typealias BookingModel = net.jupw.hubertus.api.models.Booking

fun Booking.toModel() = BookingModel(
    startTime,
    endTime,
    activity.id,
    subject,
    id,
    creationTime,
    user.id,
)