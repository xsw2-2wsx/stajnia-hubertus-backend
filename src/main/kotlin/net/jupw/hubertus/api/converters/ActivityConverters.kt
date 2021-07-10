package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.Activity

typealias ActivityModel = net.jupw.hubertus.api.models.Activity
typealias ActivityConstraintModel = net.jupw.hubertus.api.models.ActivityConstraint

fun Activity.toModel(): ActivityModel = ActivityModel(
    id = id,
    name = name,
    description = description,
    points = points.toBigDecimal(),
    constraints = constraints.map { ActivityConstraintModel(
        startTime = it.startTime.toString(),
        endTime = it.endTime.toString(),
    ) },
)
