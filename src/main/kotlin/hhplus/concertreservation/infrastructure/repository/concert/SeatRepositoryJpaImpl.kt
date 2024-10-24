package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.concert.dto.ScheduleSeatCount
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SeatRepositoryJpaImpl(
    private val seatJpaRepository: SeatJpaRepository,
) : SeatRepository {
    override fun save(seat: Seat): Seat {
        return seatJpaRepository.save(seat)
    }

    override fun findByIdOrNull(id: Long): Seat? {
        return seatJpaRepository.findByIdOrNull(id)
    }

    override fun findByIdOrNullWithLock(id: Long): Seat? {
        return seatJpaRepository.findByIdOrNullWithLock(id)
    }

    override fun findAllByScheduleId(scheduleId: Long): List<Seat> {
        return seatJpaRepository.findAllByScheduleId(scheduleId)
    }

    override fun countUnavailableSeatsGroupByScheduleId(): List<ScheduleSeatCount> {
        return seatJpaRepository.countUnavailableSeatsGroupByScheduleId()
    }
}
