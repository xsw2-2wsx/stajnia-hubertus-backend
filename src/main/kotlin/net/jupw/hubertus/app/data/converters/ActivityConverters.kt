package net.jupw.hubertus.app.data.converters

import net.jupw.hubertus.app.data.entities.ActivityConstraintEmbeddable
import net.jupw.hubertus.app.data.entities.ActivityEntity
import net.jupw.hubertus.app.entities.Activity
import net.jupw.hubertus.app.entities.ActivityConstraint
import net.jupw.hubertus.app.entities.ActivityConstraintImpl
import net.jupw.hubertus.app.entities.ActivityImpl
import java.time.LocalTime

fun ActivityEntity.toActivity() = ActivityImpl(
    id,
    name,
    description,
    points,
    constraints.map { it.toActivityConstraint() }.toSet()
)

fun Activity.toEntity() = ActivityEntity(
    0,
    name,
    description,
    points,
    constraints.map { it.toEntity() }.toSet()
)

fun ActivityConstraintEmbeddable.toActivityConstraint() = ActivityConstraintImpl(
    startTime, endTime
)

fun ActivityConstraint.toEntity() = ActivityConstraintEmbeddable(
    startTime, endTime
)

fun Pair<LocalTime, LocalTime>.toConstraint() = ActivityConstraintEmbeddable(first, second)