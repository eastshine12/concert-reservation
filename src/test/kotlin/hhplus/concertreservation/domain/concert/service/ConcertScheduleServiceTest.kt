package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.exception.ConcertScheduleNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConcertScheduleServiceTest {

    private val concertManager = mockk<ConcertManager>()
    private val concertScheduleRepository = mockk<ConcertScheduleRepository>()
    private val concertScheduleService = ConcertScheduleService(concertManager, concertScheduleRepository)

    @Test
    fun `must throw exception when no schedules are found for the concert`() {
        // given
        val concertId = 1L
        every { concertScheduleRepository.findAllByConcertId(concertId) } returns emptyList()

        // when, then
        assertThrows<ConcertScheduleNotFoundException> {
            concertScheduleService.getSchedulesByConcertId(concertId)
        }
    }

    @Test
    fun `should return schedules when found for the concert`() {
        // given
        val concertId = 1L
        val scheduleList = listOf(mockk<ConcertSchedule>(), mockk<ConcertSchedule>())
        every { concertScheduleRepository.findAllByConcertId(concertId) } returns scheduleList

        // when
        val result = concertScheduleService.getSchedulesByConcertId(concertId)

        // then
        assert(result == scheduleList)
    }
}
