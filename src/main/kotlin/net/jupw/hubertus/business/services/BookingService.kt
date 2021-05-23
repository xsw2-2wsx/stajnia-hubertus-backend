package net.jupw.hubertus.business.services

import net.jupw.hubertus.business.ConfKeys
import net.jupw.hubertus.business.Configuration
import net.jupw.hubertus.business.exceptions.ActivityTypeNotAllowedException
import net.jupw.hubertus.business.exceptions.InsufficientSpaceException
import net.jupw.hubertus.business.util.betweenExclusive
import net.jupw.hubertus.business.util.filterBetweenExclusive
import net.jupw.hubertus.business.value.Booking
import java.time.LocalDateTime

class BookingService(
    private val conf: Configuration
) {

    fun validate(newBooking: Booking, otherBookings: Collection<Booking>) {
        if(!newBooking.activityType.isAllowed(newBooking.startTime, newBooking.endTime))
            throw ActivityTypeNotAllowedException(newBooking.activityType)

        val maxPoints = conf[ConfKeys.MAX_POINTS].toDouble()

        val toCheck = otherBookings
            .filterBetweenExclusive(newBooking.startTime, newBooking.endTime)
            .toMutableList()

        toCheck.add(newBooking)

        val changePoints = sequence {
            yieldAll(toCheck.map(Booking::startTime))
            yield(newBooking.endTime)
        }
            .filterBetweenExclusive(newBooking.startTime, newBooking.endTime)
            .sorted()
            .distinct()
            .toMutableList()
        changePoints.add(newBooking.endTime)

        var rangeStart = newBooking.startTime

        println(changePoints.toList().forEach(::println))

        for(date in changePoints) {
            val pointSum = toCheck
                .filter { !it.startTime.isAfter(rangeStart) }
                .filter { it.endTime.isAfter(rangeStart) }
                .sumOf { it.activityType.points }

            if(pointSum > maxPoints) throw InsufficientSpaceException()
            rangeStart = date
        }
    }

    private fun Collection<Booking>.filterBetweenExclusive(start: LocalDateTime, end: LocalDateTime)
        = filter { it.startTime.betweenExclusive(start, end) || it.endTime.betweenExclusive(start, end) }


}