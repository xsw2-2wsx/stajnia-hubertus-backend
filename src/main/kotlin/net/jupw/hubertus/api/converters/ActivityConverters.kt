package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.Activity
import net.jupw.hubertus.app.entities.ActivityConstraint

typealias ActivityModel = net.jupw.hubertus.api.models.Activity
typealias ActivityConstraintModel = net.jupw.hubertus.api.models.ActivityConstraint

fun Activity.toModel(): ActivityModel = ActivityModel(
    id = id,
    name = name,
    description = description,
    points = points.toBigDecimal(),
)

fun ActivityConstraint.toModel() =  ActivityConstraintModel(
    startTime = startTime.toString(),
    endTime = endTime.toString(),
)
