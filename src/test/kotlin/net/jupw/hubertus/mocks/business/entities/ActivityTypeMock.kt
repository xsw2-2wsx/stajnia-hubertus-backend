package net.jupw.hubertus.mocks.business.entities

import net.jupw.hubertus.business.entities.ActivityType
import java.time.LocalDateTime

class ActivityTypeMock(
    override val displayName: String = "Mockup activity type",
    override val points: Double = 0.0,
    private val isAllowed: Boolean = true,
) : ActivityType {
    override fun isAllowed(start: LocalDateTime, end: LocalDateTime): Boolean = isAllowed
}