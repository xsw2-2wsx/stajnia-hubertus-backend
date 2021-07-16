package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.Booking
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.BookingInteractor
import net.jupw.hubertus.util.toLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BookingsController : BookingsApi {

    @Autowired
    private lateinit var bookingInteractor: BookingInteractor

    override fun bookingsBookingIdDelete(bookingId: Int): ResponseEntity<Unit> =
        bookingInteractor.cancelBooking(bookingId).toResponseEntity()

    override fun bookingsBookingIdGet(bookingId: Int): ResponseEntity<Booking> =
        bookingInteractor.getBooking(bookingId).toModel().toResponseEntity()

    override fun bookingsGet(
        rangeStart: String?,
        rangeEnd: String?,
        startTimeStart: String?,
        startTimeEnd: String?,
        endTimestart: String?,
        endTimeEnd: String?,
        creationTimeStart: String?,
        creationTimeEnd: String?,
        userId: Int?,
        activityId: Int?,
        subject: String?
    ): ResponseEntity<List<Booking>> =
        bookingInteractor.filterBookings(
            rangeStart?.toLocalDateTime(),
            rangeEnd?.toLocalDateTime(),
            startTimeStart?.toLocalDateTime(),
            startTimeEnd?.toLocalDateTime(),
            endTimestart?.toLocalDateTime(),
            endTimeEnd?.toLocalDateTime(),
            creationTimeStart?.toLocalDateTime(),
            creationTimeEnd?.toLocalDateTime(),
            userId,
            activityId,
            subject,
        ).map { it.toModel() }.toResponseEntity()

    override fun bookingsPost(booking: Booking): ResponseEntity<Unit> {
        bookingInteractor.createBooking(
            booking.startTime,
            booking.endTime,
            booking.activityId,
            booking.subject
        )
        return ResponseEntity.ok().build()
    }

    override fun bookingsTimeGet(): ResponseEntity<List<String>> =
        bookingInteractor.getAllowedTimes().map { it.toString() }
            .toResponseEntity()
}