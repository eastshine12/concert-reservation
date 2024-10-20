package hhplus.concertreservation.application.concert

import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.dto.command.ReservationCommand
import hhplus.concertreservation.domain.concert.dto.info.ConcertInfo
import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(classes = [ConcertReservationApplication::class])
class ConcertFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var concertFacade: ConcertFacade
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var waitingQueue: WaitingQueue
    private lateinit var concert1: Concert
    private lateinit var schedule1: ConcertSchedule
    private lateinit var schedule2: ConcertSchedule

    @BeforeEach
    fun setUp() {
        user1 = userJpaRepository.save(User(name = "User1", email = "user1@test.com", balance = 100000.toBigDecimal()))
        user2 = userJpaRepository.save(User(name = "User2", email = "user2@test.com", balance = 100000.toBigDecimal()))
        userJpaRepository.save(User(name = "User3", email = "user3@test.com", balance = 100000.toBigDecimal()))
        userJpaRepository.save(User(name = "User4", email = "user4@test.com", balance = 100000.toBigDecimal()))
        userJpaRepository.save(User(name = "User5", email = "user5@test.com", balance = 100000.toBigDecimal()))

        concert1 = concertJpaRepository.save(Concert(title = "Concert 1", duration = 120))

        schedule1 =
            concertScheduleJpaRepository.save(
                ConcertSchedule(
                    concertId = concert1.id,
                    startTime = LocalDateTime.now(),
                    totalSeats = 5,
                    availableSeats = 5,
                ),
            )
        schedule2 =
            concertScheduleJpaRepository.save(
                ConcertSchedule(
                    concertId = concert1.id,
                    startTime = LocalDateTime.now().plusDays(1),
                    totalSeats = 5,
                    availableSeats = 5,
                ),
            )

        createSeatsForSchedule(schedule1)
        createSeatsForSchedule(schedule2)

        waitingQueue =
            waitingQueueJpaRepository.save(
                WaitingQueue(
                    scheduleId = schedule1.id,
                    token = "123e4567-e89b-12d3-a456-426614174000",
                    status = QueueStatus.ACTIVE,
                    queuePosition = 1,
                    expiresAt = LocalDateTime.now().plusMinutes(10),
                ),
            )
    }

    private fun createSeatsForSchedule(schedule: ConcertSchedule) {
        repeat(5) { seatNumber ->
            seatJpaRepository.save(
                Seat(
                    scheduleId = schedule.id,
                    seatNumber = seatNumber + 1,
                    price = BigDecimal("10000.00"),
                    status = SeatStatus.AVAILABLE,
                ),
            )
        }
    }

    @Test
    fun `must retrieve available reservation dates`() {
        // given
        val concertId = 1L
        val token = "123e4567-e89b-12d3-a456-426614174000"
        // when
        val concertInfo: ConcertInfo = concertFacade.getReservationAvailableDates(token, concertId)

        // then
        assertNotNull(concertInfo)
        assertEquals(concertId, concertInfo.id)
        assertTrue(concertInfo.schedules.isNotEmpty())
    }

    @Test
    fun `must retrieve available seats for a schedule`() {
        // given
        val scheduleId = 1L
        val token = "123e4567-e89b-12d3-a456-426614174000"

        // when
        val seats: List<SeatInfo> = concertFacade.getSeatsInfo(token, scheduleId)

        // then
        assertNotNull(seats)
        assertTrue(seats.isNotEmpty())
        assertEquals(BigDecimal("10000.00"), seats.first().price)
    }

    @Test
    fun `must create pending reservation`() {
        // given
        val command =
            ReservationCommand(
                userId = 1L,
                scheduleId = 1L,
                seatId = 1L,
                token = "123e4567-e89b-12d3-a456-426614174000",
            )

        // when
        val reservationInfo: ReservationInfo = concertFacade.createReservation(command)

        // then
        assertNotNull(reservationInfo)
        assertEquals(true, reservationInfo.success)
        assertEquals(1L, reservationInfo.reservationId)

        assertEquals(SeatStatus.UNAVAILABLE, seatJpaRepository.findById(1L).get().status)
        assertEquals(4, concertScheduleJpaRepository.findById(1L).get().availableSeats)
        assertEquals(ReservationStatus.PENDING, reservationJpaRepository.findById(1L).get().status)
    }

    @Test
    fun `must create pending reservation for 5 users concurrently`() {
        // given
        val scheduleId = 1L
        val token = "123e4567-e89b-12d3-a456-426614174000"
        val userIdList = listOf(1L, 2L, 3L, 4L, 5L)
        val successCount = AtomicInteger(0)
        val commands =
            userIdList.map { userId ->
                ReservationCommand(
                    userId = userId,
                    scheduleId = scheduleId,
                    seatId = userId,
                    token = token,
                )
            }
        val executor: ExecutorService = Executors.newFixedThreadPool(5)

        // when
        val tasks =
            commands.map { command ->
                Callable {
                    try {
                        concertFacade.createReservation(command)
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        println("Reservation failed for userId: ${command.userId}, reason: ${e.message}")
                    }
                }
            }

        executor.invokeAll(tasks)
        executor.shutdown()

        // then
        assertEquals(5, successCount.get())
        repeat(5) {
            assertEquals(SeatStatus.UNAVAILABLE, seatJpaRepository.findById((it + 1).toLong()).get().status)
        }
        repeat(5) {
            assertEquals(ReservationStatus.PENDING, reservationJpaRepository.findById((it + 1).toLong()).get().status)
        }
//        assertEquals(0, concertScheduleJpaRepository.findById(1L).get().availableSeats)
    }
}
