package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.concert.exception.InvalidReservationStatusException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class ReservationTest {

    @Test
    fun `must throw exception when confirming a reservation not in pending state`() {
        // given
        val reservation = Reservation(
            userId = 1L,
            scheduleId = 1L,
            seatId = 1L,
            status = ReservationStatus.CONFIRMED,
            expiresAt = LocalDateTime.now().plusMinutes(10)
        )

        // when & then
        val exception = assertThrows<InvalidReservationStatusException> {
            reservation.confirm()
        }

        // then
        assertEquals("Reservation is not in pending state", exception.message)
    }

    @Test
    fun `must throw exception when canceling a confirmed reservation`() {
        // given
        val reservation = Reservation(
            userId = 1L,
            scheduleId = 1L,
            seatId = 1L,
            status = ReservationStatus.CONFIRMED,
            expiresAt = LocalDateTime.now().plusMinutes(10)
        )

        // when & then
        val exception = assertThrows<InvalidReservationStatusException> {
            reservation.cancel()
        }

        // then
        assertEquals("Confirmed reservations cannot be canceled.", exception.message)
    }
}
