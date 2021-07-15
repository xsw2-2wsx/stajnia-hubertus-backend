package net.jupw.hubertus.app.exceptions

import java.time.LocalDateTime

class BookingHoursNotAllowedException(start: LocalDateTime, end: LocalDateTime)
    : ApplicationException("Booking from $start to $end not permitted due to app configuration (invalid times)")