package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.common.exception.CoreException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class SeatTest {
    @Test
    fun `must throw exception when reserving a seat that is not available`() {
        // given
        val seat =
            Seat(
                scheduleId = 1L,
                seatNumber = 1,
                price = BigDecimal(70_000),
                status = SeatStatus.UNAVAILABLE,
            )

        // when & then
        val exception =
            assertThrows<CoreException> {
                seat.reserve()
            }

        // then
        assertEquals("The seat is not available for reservation.", exception.message)
    }

    @Test
    fun `must throw exception when marking an already available seat as available`() {
        // given
        val seat =
            Seat(
                scheduleId = 1L,
                seatNumber = 1,
                price = BigDecimal(70_000),
                status = SeatStatus.AVAILABLE,
            )

        // when & then
        val exception =
            assertThrows<CoreException> {
                seat.markAsAvailable()
            }

        // then
        assertEquals("Seat is already available.", exception.message)
    }
}
