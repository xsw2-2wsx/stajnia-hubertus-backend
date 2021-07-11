package net.jupw.hubertus.app.entities

import net.jupw.hubertus.business.entities.ActivityType
import java.time.LocalTime

interface Activity : ActivityType {

    var id: Int

    var name: String

    override val displayName: String
        get() = name

    override var description: String

    override var points: Double

    var constraints: Set<ActivityConstraint>

}