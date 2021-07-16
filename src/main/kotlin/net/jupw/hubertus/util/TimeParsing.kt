package net.jupw.hubertus.util

import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class InvalidTimeException(val time: String) : Exception("Could not parse $time")

fun String.toLocalDateTime(): LocalDateTime = try {
    LocalDateTime.parse(this)
} catch (e: DateTimeParseException) {
    throw InvalidTimeException(this)
}