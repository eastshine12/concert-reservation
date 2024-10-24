package hhplus.concertreservation.domain.concert.repository

import hhplus.concertreservation.domain.concert.dto.ScheduleSeatCount
import hhplus.concertreservation.domain.concert.entity.Seat

interface SeatRepository {
    fun save(seat: Seat): Seat

    fun findByIdOrNull(id: Long): Seat?

    fun findByIdOrNullWithLock(id: Long): Seat?

    fun findAllByScheduleId(scheduleId: Long): List<Seat>

    fun countUnavailableSeatsGroupByScheduleId(): List<ScheduleSeatCount>
}
