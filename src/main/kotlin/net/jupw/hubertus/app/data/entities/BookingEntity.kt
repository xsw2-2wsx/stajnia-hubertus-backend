package net.jupw.hubertus.app.data.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "bookings")
class BookingEntity(

    @Id
    @GeneratedValue
    @Column(name = "bookings_id")
    var id: Int,

    @Column(name = "creation_time")
    var creationTime: LocalDateTime,

    @Column(name = "start_time")
    var startTime: LocalDateTime,

    @Column(name = "end_time")
    var endTime: LocalDateTime,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activities_id")
    var activity: ActivityEntity,

    @Column(name = "subject")
    var subject: String,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    var user: UserEntity,

)