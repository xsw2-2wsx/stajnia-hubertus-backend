package net.jupw.hubertus.app.entities

import net.jupw.hubertus.business.value.Booking
import java.time.LocalDateTime

interface Booking : Booking {
    var id: Int

    var creationTime: LocalDateTime

    override var startTime: LocalDateTime

    override var endTime: LocalDateTime

    var activity: Activity

    override var subject: String

    var user: User
}