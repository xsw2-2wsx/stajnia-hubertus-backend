package net.jupw.hubertus.app.entities

import java.time.LocalTime

interface ActivityConstraint {

    var startTime: LocalTime

    var endTime: LocalTime
}