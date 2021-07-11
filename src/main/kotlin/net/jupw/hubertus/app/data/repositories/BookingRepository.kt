package net.jupw.hubertus.app.data.repositories

import net.jupw.hubertus.app.data.entities.BookingEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BookingRepository : CrudRepository<BookingEntity, Int> {

    @Query("SELECT * FROM bookings b WHERE b.users_id = :id", nativeQuery = true)
    fun findAllByUserId(id: Int): Set<BookingEntity>

    @Query("""
        SELECT DISTINCT * 
        FROM bookings 
        WHERE
            start_time > :start and start_time < :end or
            end_time > :start and end_time < :end
    """, nativeQuery = true)
    fun findForRange(start: LocalDateTime, end: LocalDateTime): Set<BookingEntity>

}