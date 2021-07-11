package net.jupw.hubertus.app.entities

import java.time.*

class ActivityImpl(
    override var id: Int,
    override var name: String,
    override var description: String,
    override var points: Double,
    override var constraints: Set<ActivityConstraint>
) : Activity {
    override fun isAllowed(start: LocalDateTime, end: LocalDateTime): Boolean {
        var currentTime = start
        var constraint: ActivityConstraint? = null

        while(currentTime.isBefore(end)) {
            constraint = constraints
                .filter { currentTime.toLocalTime() in it }
                .firstOrNull { it != constraint }
                ?: return false
            var duration =  Duration.between(currentTime.toLocalTime(), constraint.endTime).abs()
            if(!currentTime.toLocalTime().isBefore(constraint.endTime))
                duration = Duration.ofHours(24) - duration
            currentTime += duration
        }
        return true
    }

    private operator fun ActivityConstraint.contains(time: LocalTime): Boolean = when {
        startTime.isBefore(endTime) -> !startTime.isAfter(time) && !endTime.isBefore(time)
        startTime.isAfter(endTime) -> !(endTime.isBefore(time) && startTime.isAfter(time))
        else -> time == startTime
    }
}