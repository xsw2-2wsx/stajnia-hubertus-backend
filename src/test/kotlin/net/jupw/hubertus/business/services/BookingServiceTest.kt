package net.jupw.hubertus.business.services

import net.jupw.hubertus.business.ConfKeys
import net.jupw.hubertus.business.Configuration
import net.jupw.hubertus.business.entities.ActivityType
import net.jupw.hubertus.business.entities.BookingOwner
import net.jupw.hubertus.business.exceptions.ActivityTypeNotAllowedException
import net.jupw.hubertus.business.exceptions.InsufficientSpaceException
import net.jupw.hubertus.business.exceptions.NotEnoughPrecedenceException
import net.jupw.hubertus.business.value.Booking
import net.jupw.hubertus.mocks.business.entities.ActivityTypeMock
import net.jupw.hubertus.mocks.business.value.bookingMock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import org.mockito.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal class BookingServiceTest {


    private val configuration: Configuration = mock {
        on { get(ConfKeys.MAX_POINTS) } doReturn "6"
        on { get(ConfKeys.MIN_BOOKING_PRECEDENCE_HOURS) } doReturn "0"
    }

    private var bookingService: BookingService = BookingService(configuration)

    @Test
    fun `Throw exception when Activity is not allowed`() {
        val activity = ActivityTypeMock(isAllowed = false)

        val booking = Booking(LocalDateTime.now(), LocalDateTime.now(), activity, "test", object : BookingOwner{})

        assertThrows<ActivityTypeNotAllowedException> {
            bookingService.validate(booking, emptyList())
        }
    }

    @Test
    fun `Test BookingService`() {
        val newBooking = bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 30)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 15)),
            ActivityTypeMock(points = 3.0),
        )

        val bookings = listOf(
            bookingMock(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
                ActivityTypeMock(points = 3.0),
            ),
        ).toMutableList()

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 30)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(4, 0)),
            ActivityTypeMock(points = 50.0),
        ))

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 33)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)),
            ActivityTypeMock(points = 50.0),
        ))

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 30)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
            ActivityTypeMock(points = 1.0),
        ))

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0)),
            ActivityTypeMock(points = 1.0),
        ))

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 15)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 15)),
            ActivityTypeMock(points = 3.0),
        ))

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }

        // Fails

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 10)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 15)),
            ActivityTypeMock(points = 3.0),
        ))

        assertThrows<InsufficientSpaceException> {
            bookingService.validate(newBooking, bookings)
        }

        bookings.removeLast()

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 45)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 55)),
            ActivityTypeMock(points = 2.1),
        ))

        assertThrows<InsufficientSpaceException> {
            bookingService.validate(newBooking, bookings)
        }

        bookings.removeLast()

        bookings.add(bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 45)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)),
            ActivityTypeMock(points = 1.0),
        ))

    }

    @Test
    fun `Test same range`() {
        val newBooking = bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
            ActivityTypeMock(points = 1.0),
        )

        val bookings = listOf(
            bookingMock(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                ActivityTypeMock(points = 3.0),
            ),
            bookingMock(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                ActivityTypeMock(points = 3.0),
            ),
        ).toMutableList()

        assertThrows<InsufficientSpaceException> {
            bookingService.validate(newBooking, bookings)
        }
    }

    @Test
    fun `Test precedence`() {
        whenever(configuration[ConfKeys.MIN_BOOKING_PRECEDENCE_HOURS]) doReturn Duration.ofHours(2).toMillis().toString()

        assertDoesNotThrow {
            bookingService.validate(bookingMock(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                ActivityTypeMock()
            ), emptyList())
        }

        assertDoesNotThrow {
            bookingService.validate(bookingMock(
                LocalDateTime.now().plusHours(2).plusSeconds(1),
                LocalDateTime.now().plusHours(4),
                ActivityTypeMock()
            ), emptyList())
        }

        assertThrows<NotEnoughPrecedenceException> {
            bookingService.validate(bookingMock(
                LocalDateTime.now().plusHours(2).minusSeconds(1),
                LocalDateTime.now().plusHours(5),
                ActivityTypeMock()
            ), emptyList())
        }
    }

    @Test
    fun `Test continuous`() {
        val newBooking = bookingMock(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
            ActivityTypeMock(points = 3.0),
        )

        val bookings = listOf(
            bookingMock(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                ActivityTypeMock(points = 6.0),
            ),
        ).toMutableList()

        assertDoesNotThrow {
            bookingService.validate(newBooking, bookings)
        }
    }

}