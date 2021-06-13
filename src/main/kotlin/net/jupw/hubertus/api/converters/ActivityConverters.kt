package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.Activity

typealias ActivityModel = net.jupw.hubertus.api.models.Activity
typealias ActivityConstraintModel = net.jupw.hubertus.api.models.ActivityConstraint

fun Activity.toModel(): ActivityModel = ActivityModel().also {
    it.id = id
    it.name = name
    it.description = description
    it.points = points.toBigDecimal()
    it.constraints = constraints.map { ActivityConstraintModel().apply {
        startTime = it.startTime.toString()
        endTime = it.endTime.toString()
    } }
}
