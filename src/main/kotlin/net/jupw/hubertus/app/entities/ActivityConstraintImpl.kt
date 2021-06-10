package net.jupw.hubertus.app.entities

import java.time.LocalTime

class ActivityConstraintImpl(
    override var id: Int,
    override var startTime: LocalTime,
    override var endTime: LocalTime,
) : ActivityConstraint