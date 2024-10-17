package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ConcertScheduleRepositoryJpaImpl(
    private val concertScheduleJpaRepository: ConcertScheduleJpaRepository
) : ConcertScheduleRepository {
    override fun save(concertSchedule: ConcertSchedule): ConcertSchedule {
        return concertScheduleJpaRepository.save(concertSchedule)
    }

    override fun findByIdOrNull(id: Long): ConcertSchedule? {
        return concertScheduleJpaRepository.findByIdOrNull(id)
    }

    override fun findAllByConcertId(concertId: Long): List<ConcertSchedule> {
        return concertScheduleJpaRepository.findAllByConcertId(concertId)
    }
}
