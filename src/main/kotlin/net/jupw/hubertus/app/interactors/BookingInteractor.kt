package net.jupw.hubertus.app.interactors

import net.jupw.hubertus.app.configuration.ConfKeys
import net.jupw.hubertus.app.configuration.Configuration
import net.jupw.hubertus.app.data.converters.toBooking
import net.jupw.hubertus.app.data.converters.toEntity
import net.jupw.hubertus.app.data.entities.BookingEntity
import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.data.repositories.BookingRepository
import net.jupw.hubertus.app.entities.Booking
import net.jupw.hubertus.app.exceptions.BookingDoesNotExistException
import net.jupw.hubertus.app.exceptions.BookingHoursNotAllowedException
import net.jupw.hubertus.app.exceptions.NotBookingOwnerException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
class BookingInteractor {

    @Autowired
    private lateinit var configuration: Configuration

    @Autowired
    private lateinit var userInteractor: UserInteractor

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @Autowired
    private lateinit var activityInteractor: ActivityInteractor

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun filterBookings(
        rangeStart: LocalDateTime? = null,
        rangeEnd: LocalDateTime? = null,
        startTimeStart: LocalDateTime? = null,
        startTimeEnd: LocalDateTime? = null,
        endTimeStart: LocalDateTime? = null,
        endTimeEnd: LocalDateTime? = null,
        creationTimeStart: LocalDateTime? = null,
        creationTimeEnd: LocalDateTime? = null,
        userId: Int? = null,
        activityId: Int? = null,
        subject: String? = null,
    ): Set<Booking> =
        bookingRepository.filterBookings(
            rangeStart, rangeEnd,
            startTimeStart, startTimeEnd,
            endTimeStart, endTimeEnd,
            creationTimeStart, creationTimeEnd,
            userId, activityId, subject)
        .map { it.toBooking() }.toSet()


    fun getBooking(id: Int): Booking = bookingRepository.findByIdOrNull(id)?.toBooking()?: throw BookingDoesNotExistException(id)

    fun createBooking(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        activityId: Int,
        subject: String
    ) {
        if(!getAllowedTimes().containsAll(listOf(startTime.toLocalTime(), endTime.toLocalTime())))
            throw BookingHoursNotAllowedException(startTime, endTime)

        bookingRepository.save(BookingEntity(
            0,
            LocalDateTime.now(),
            startTime, endTime,
            activityInteractor.findActivityById(activityId).toEntity(),
            subject,
            entityManager.getReference(UserEntity::class.java, userInteractor.authenticatedUser.id)
        ))
    }

    fun cancelBooking(id: Int) {
        val booking = bookingRepository.findByIdOrNull(id)?: throw BookingDoesNotExistException(id)
        if(booking.user.id != userInteractor.authenticatedUser.id) throw NotBookingOwnerException(id, userInteractor.authenticatedUser.id)

        bookingRepository.deleteById(id)
    }

    fun deleteBooking(id: Int) = bookingRepository.deleteById(id)

    fun getAllowedTimes(): Collection<LocalTime> {
        val startTime = LocalTime.parse(configuration[ConfKeys.BOOKING_HOURS_START])
        val interval = Duration.ofMillis(configuration[ConfKeys.BOOKING_INTERVAL_MS].toLong())
        val endTime = LocalTime.parse(configuration[ConfKeys.BOOKING_HORUS_END])

        return generateSequence(startTime) { it.plus(interval) }
            .takeWhile { !it.isAfter(endTime) }
            .toSet()
    }

}