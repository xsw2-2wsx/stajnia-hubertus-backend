package net.jupw.hubertus.app.data.repositories

import net.jupw.hubertus.app.data.entities.BookingEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BookingRepository : CrudRepository<BookingEntity, Int> {

    @Query("""
        SELECT DISTINCT * 
        FROM bookings 
        WHERE
            (:rangeStart is null or :rangeEnd is null or (
                (start_time > :rangeStart and start_time < :rangeEnd) or
                (end_time > :rangeStart and end_time < :rangeEnd)
            )) and
            (:startTimeStart is null or start_time >= :startTimeStart) and
            (:startTimeEnd is null or start_time <= :startTimeEnd) and
            (:endTimeStart is null or end_time >= :endTimeStart) and
            (:endTimeEnd is null or end_time <= :endTimeEnd) and
            (:creationTimeEnd is null or creation_time >= :creationTimeStart) and
            (:creationTimeEnd is null or creation_time <= :creationTimeEnd) and
            (:userId is null or users_id = :userId) and
            (:activityId is null or activities_id = :activityId) and
            (:subject is null or subject = :subject)
       
    """, nativeQuery = true)
    fun filterBookings(
        rangeStart: LocalDateTime? = null,
        rangeEnd: LocalDateTime? = null,
        startTimeStart: LocalDateTime? = null,
        startTimeEnd: LocalDateTime? = null,
        endTimeStart: LocalDateTime? = null,
        endTimeEnd: LocalDateTime? = null,
        creationTimeStart: LocalDateTime? = null,
        creationTimeEnd: LocalDateTime? = null,
        userId: Int? = null,
        activityId: Int? = null,
        subject: String? = null,
    ): Set<BookingEntity>

}