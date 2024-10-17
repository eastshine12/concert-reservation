package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.exception.SeatsNotFoundException
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SeatServiceTest {

    private val seatRepository = mockk<SeatRepository>()
    private val seatService = SeatService(seatRepository)

    @Test
    fun `must throw exception when seat is not found by id`() {
        // given
        val seatId = 1L
        every { seatRepository.findByIdOrNull(seatId) } returns null

        // when, then
        assertThrows<SeatsNotFoundException> {
            seatService.getSeatById(seatId)
        }
    }

    @Test
    fun `must throw exception when no seats are found for schedule`() {
        // given
        val scheduleId = 1L
        every { seatRepository.findAllByScheduleId(scheduleId) } returns emptyList()

        // when, then
        assertThrows<SeatsNotFoundException> {
            seatService.getSeatsByScheduleId(scheduleId)
        }
    }
}
