package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.concert.dto.ScheduleSeatCount
import hhplus.concertreservation.domain.concert.entity.Seat
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface SeatJpaRepository : JpaRepository<Seat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = :id")
    fun findByIdOrNullWithLock(id: Long): Seat?

    fun findAllByScheduleId(scheduleId: Long): List<Seat>

    @Query(
        """
        select new hhplus.concertreservation.domain.concert.dto.ScheduleSeatCount(s.scheduleId, COUNT(s)) 
        from Seat s 
        where s.status = 'UNAVAILABLE'
        group by s.scheduleId
    """,
    )
    fun countUnavailableSeatsGroupByScheduleId(): List<ScheduleSeatCount>
}
