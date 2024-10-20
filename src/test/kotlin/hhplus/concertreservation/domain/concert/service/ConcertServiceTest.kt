package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.exception.ConcertNotFoundException
import hhplus.concertreservation.domain.concert.exception.ConcertScheduleNotFoundException
import hhplus.concertreservation.domain.concert.exception.SeatsNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConcertServiceTest {
    private val concertManager = mockk<ConcertManager>()
    private val concertRepository = mockk<ConcertRepository>()
    private val concertScheduleRepository = mockk<ConcertScheduleRepository>()
    private val seatRepository = mockk<SeatRepository>()
    private val concertService = ConcertService(concertManager, concertRepository, concertScheduleRepository, seatRepository)

    @Test
    fun `must throw exception when concert is not found`() {
        // given
        val concertId = 1L
        every { concertRepository.findByIdOrNull(concertId) } returns null

        // when, then
        assertThrows<ConcertNotFoundException> {
            concertService.getConcertById(concertId)
        }
    }

    @Test
    fun `must throw exception when no schedules are found for the concert`() {
        // given
        val concertId = 1L
        every { concertScheduleRepository.findAllByConcertId(concertId) } returns emptyList()

        // when, then
        assertThrows<ConcertScheduleNotFoundException> {
            concertService.getSchedulesByConcertId(concertId)
        }
    }

    @Test
    fun `should return schedules when found for the concert`() {
        // given
        val concertId = 1L
        val scheduleList = listOf(mockk<ConcertSchedule>(), mockk<ConcertSchedule>())
        every { concertScheduleRepository.findAllByConcertId(concertId) } returns scheduleList

        // when
        val result = concertService.getSchedulesByConcertId(concertId)

        // then
        assert(result == scheduleList)
    }

    @Test
    fun `must throw exception when seat is not found by id`() {
        // given
        val seatId = 1L
        every { seatRepository.findByIdOrNull(seatId) } returns null

        // when, then
        assertThrows<SeatsNotFoundException> {
            concertService.getSeatById(seatId)
        }
    }

    @Test
    fun `must throw exception when no seats are found for schedule`() {
        // given
        val scheduleId = 1L
        every { seatRepository.findAllByScheduleId(scheduleId) } returns emptyList()

        // when, then
        assertThrows<SeatsNotFoundException> {
            concertService.getSeatsByScheduleId(scheduleId)
        }
    }
}
