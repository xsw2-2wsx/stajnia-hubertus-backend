package net.jupw.hubertus.business.services

import net.jupw.hubertus.business.ConfKeys
import net.jupw.hubertus.business.Configuration
import net.jupw.hubertus.business.entities.ActivityType
import net.jupw.hubertus.business.entities.BookingOwner
import net.jupw.hubertus.business.exceptions.ActivityTypeNotAllowedException
import net.jupw.hubertus.business.exceptions.InsufficientSpaceException
import net.jupw.hubertus.business.value.Booking
import net.jupw.hubertus.mocks.business.entities.ActivityTypeMock
import net.jupw.hubertus.mocks.business.value.bookingMock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal class BookingServiceTest {


    private val configuration: Configuration = mock {
        on { get(ConfKeys.MAX_POINTS) } doReturn "6"
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



}