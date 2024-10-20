package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.concert.exception.SeatAvailabilityException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Test

class ConcertScheduleTest {

    @Test
    fun `must throw exception when decreasing available seats below zero`() {
        // given
        val concertSchedule = ConcertSchedule(
            concertId = 1L,
            startTime = LocalDateTime.now(),
            totalSeats = 50,
            availableSeats = 0
        )

        // when & then
        val exception = assertThrows<SeatAvailabilityException> {
            concertSchedule.occupySeat()
        }

        // then
        assertEquals("No available seats left to reserve.", exception.message)
    }

    @Test
    fun `must throw exception when increasing seats beyond total seats`() {
        // given
        val concertSchedule = ConcertSchedule(
            concertId = 1L,
            startTime = LocalDateTime.now(),
            totalSeats = 50,
            availableSeats = 50
        )

        // when & then
        val exception = assertThrows<SeatAvailabilityException> {
            concertSchedule.restoreSeat()
        }

        // then
        assertEquals("All seats are already available. Cannot increase available seats.", exception.message)
    }
}