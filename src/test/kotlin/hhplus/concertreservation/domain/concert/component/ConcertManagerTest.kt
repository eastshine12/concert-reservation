package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.config.ReservationProperties
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class ConcertManagerTest {
    private val scheduleRepository = mockk<ConcertScheduleRepository>()
    private val reservationRepository = mockk<ReservationRepository>()
    private val reservationProperties = mockk<ReservationProperties>()

    private val concertManager = ConcertManager(scheduleRepository, reservationRepository, reservationProperties)

    @Test
    fun `must throw exception when concert schedule not found by id`() {
        // given
        val scheduleId = 1L
        every { scheduleRepository.findByIdOrNull(scheduleId) } returns null

        // when & then
        val exception =
            assertThrows<CoreException> {
                concertManager.getScheduleById(scheduleId)
            }

        // then
        assertEquals("No concert schedule found for the given ID.", exception.message)
    }

    @Test
    fun `must throw exception when reservation not found by id`() {
        // given
        val reservationId = 1L
        every { reservationRepository.findByIdOrNullWithLock(reservationId) } returns null

        // when & then
        val exception =
            assertThrows<CoreException> {
                concertManager.getReservationWithLock(reservationId)
            }

        // then
        assertEquals("No reservation found for the given ID.", exception.message)
    }

    @Test
    fun `must create pending reservation with expiration time`() {
        // given
        val scheduleId = 1L
        val userId = 1L
        val seatId = 1L
        val expiresAt = LocalDateTime.now().plusMinutes(30)
        every { reservationProperties.expireMinutes } returns 30L

        val reservation =
            Reservation(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId,
                status = ReservationStatus.PENDING,
                expiresAt = expiresAt,
            )
        every { reservationRepository.save(any()) } returns reservation

        // when
        val createdReservation = concertManager.createPendingReservation(userId, scheduleId, seatId)

        // then
        verify { reservationRepository.save(any()) }
        assertEquals(ReservationStatus.PENDING, createdReservation.status)
        assertNotNull(createdReservation.expiresAt)
    }
}
