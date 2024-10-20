package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.SeatNotFoundException
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class SeatFinderTest {
    private val seatRepository: SeatRepository = mockk()
    private val seatFinder = SeatFinder(seatRepository)

    @Test
    fun `must throw exception when seat not found by id with lock`() {
        // given
        val seatId = 1L
        every { seatRepository.findByIdOrNullWithLock(seatId) } returns null

        // when & then
        val exception =
            assertThrows<SeatNotFoundException> {
                seatFinder.getSeatWithLock(seatId)
            }

        // then
        assertEquals("Seat with id $seatId not found", exception.message)
    }

    @Test
    fun `must throw exception when available seat does not belong to the schedule`() {
        // given
        val seatId = 1L
        val scheduleId = 2L
        val seat =
            Seat(
                scheduleId = 3L,
                seatNumber = 1,
                price = BigDecimal.valueOf(100.0),
                status = SeatStatus.AVAILABLE,
            )
        every { seatRepository.findByIdOrNullWithLock(seatId) } returns seat

        // when & then
        val exception =
            assertThrows<IllegalStateException> {
                seatFinder.getAvailableSeatWithLock(scheduleId, seatId)
            }

        // then
        assertEquals("The seat is either unavailable or does not belong to the schedule.", exception.message)
    }

    @Test
    fun `must throw exception when seat is not available for reservation`() {
        // given
        val seatId = 1L
        val scheduleId = 1L
        val seat =
            Seat(
                scheduleId = scheduleId,
                seatNumber = 1,
                price = BigDecimal.valueOf(100.0),
                status = SeatStatus.UNAVAILABLE,
            )
        every { seatRepository.findByIdOrNullWithLock(seatId) } returns seat

        // when & then
        val exception =
            assertThrows<IllegalStateException> {
                seatFinder.getAvailableSeatWithLock(scheduleId, seatId)
            }

        // then
        assertEquals("The seat is either unavailable or does not belong to the schedule.", exception.message)
    }
}
