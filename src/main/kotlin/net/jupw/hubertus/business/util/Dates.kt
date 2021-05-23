package net.jupw.hubertus.business.util

import net.jupw.hubertus.business.value.Booking
import java.time.LocalDateTime

fun LocalDateTime.betweenExclusive(start: LocalDateTime, end: LocalDateTime): Boolean = isAfter(start) && isBefore(end)

fun <T : Iterable<LocalDateTime>> T.filterBetweenExclusive(start: LocalDateTime, end: LocalDateTime
    ): Iterable<LocalDateTime> = filter { it.betweenExclusive(start, end) }

fun <T : Sequence<LocalDateTime>> T.filterBetweenExclusive(start: LocalDateTime, end: LocalDateTime
): Sequence<LocalDateTime> = filter { it.betweenExclusive(start, end) }

