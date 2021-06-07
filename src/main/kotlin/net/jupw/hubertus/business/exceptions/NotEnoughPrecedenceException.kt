package net.jupw.hubertus.business.exceptions

import net.jupw.hubertus.business.value.Booking
import java.time.Duration

class NotEnoughPrecedenceException(
    val booking: Booking,
    val duration: Duration,
    val requiredDuration: Duration

) : BookingException("Not enough precedence: $duration. Required minimum: $requiredDuration")