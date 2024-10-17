package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertScheduleJpaRepository : JpaRepository<ConcertSchedule, Long> {
    fun findAllByConcertId(concertId: Long): List<ConcertSchedule>
}
