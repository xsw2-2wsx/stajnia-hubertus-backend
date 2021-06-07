package net.jupw.hubertus.app.entities

import net.jupw.hubertus.business.entities.ActivityType

interface Activity : ActivityType {

    var name: String

    override val displayName: String
        get() = name

    override var description: String

    override var points: Double
}