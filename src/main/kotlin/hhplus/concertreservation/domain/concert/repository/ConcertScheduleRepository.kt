package hhplus.concertreservation.domain.concert.repository

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule

interface ConcertScheduleRepository {
    fun save(concertSchedule: ConcertSchedule): ConcertSchedule

    fun saveAll(schedules: MutableList<ConcertSchedule>): MutableList<ConcertSchedule>

    fun findByIdOrNull(id: Long): ConcertSchedule?

    fun findAllByConcertId(concertId: Long): List<ConcertSchedule>

    fun findAll(): List<ConcertSchedule>
}
