package net.jupw.hubertus.business.entities

import net.jupw.hubertus.util.Displayable
import java.time.LocalDateTime

interface ActivityType : Displayable {
    val points: Double

    fun isAllowed(start: LocalDateTime, end: LocalDateTime): Boolean
}