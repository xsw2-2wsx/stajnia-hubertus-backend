package net.jupw.hubertus.app.data.entities

import java.time.LocalTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ActivityConstraintEmbeddable(

    @Column(name = "time_start")
    var startTime: LocalTime,

    @Column(name = "time_end")
    var endTime: LocalTime,

)