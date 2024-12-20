package hhplus.concertreservation.application.concert

import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.domain.common.enums.OutboxStatus
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.dto.command.ReservationCommand
import hhplus.concertreservation.domain.concert.dto.info.ConcertInfo
import hhplus.concertreservation.domain.concert.dto.info.CreateReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.util.RedisTestHelper
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(classes = [ConcertReservationApplication::class])
class ConcertFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var concertFacade: ConcertFacade
    private lateinit var redisTestHelper: RedisTestHelper
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var waitingQueue1: WaitingQueue
    private lateinit var waitingQueue2: WaitingQueue
    private lateinit var waitingQueue3: WaitingQueue
    private lateinit var concert1: Concert
    private lateinit var schedule1: ConcertSchedule
    private lateinit var schedule2: ConcertSchedule
    private lateinit var schedule3: ConcertSchedule

    @BeforeEach
    fun setUp() {
        redisTestHelper = RedisTestHelper(redisTemplate)

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
                    availableSeats = 0,
                ),
            )
        schedule3 =
            concertScheduleJpaRepository.save(
                ConcertSchedule(
                    concertId = concert1.id,
                    startTime = LocalDateTime.now().plusDays(1),
                    totalSeats = 5,
                    availableSeats = 5,
                ),
            )

        // create seats
        createSeatsForSchedule(schedule1)
        createSeatsForSchedule(schedule2)
        createSeatsForSchedule(schedule3)

        waitingQueue1 =
            WaitingQueue(
                scheduleId = schedule1.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
                status = QueueStatus.ACTIVE,
                expiresAt = LocalDateTime.now().plusMinutes(10),
            )

        waitingQueue2 =
            WaitingQueue(
                scheduleId = schedule2.id,
                token = "123e4567-e89b-12d3-a456-426614174001",
                status = QueueStatus.ACTIVE,
                expiresAt = LocalDateTime.now().plusMinutes(10),
            )
        waitingQueue3 =
            WaitingQueue(
                scheduleId = schedule3.id,
                token = "123e4567-e89b-12d3-a456-426614174002",
                status = QueueStatus.ACTIVE,
                expiresAt = LocalDateTime.now().minusMinutes(10),
            )
        redisTestHelper.saveTokenAndInfo(waitingQueue1)
        redisTestHelper.saveTokenAndInfo(waitingQueue2)
        redisTestHelper.saveTokenAndInfo(waitingQueue3)
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
                scheduleId = 3L,
                seatId = 11L,
                token = "123e4567-e89b-12d3-a456-426614174002",
            )

        // when
        val createReservationInfo: CreateReservationInfo = concertFacade.createReservation(command)

        // consume 대기
        await()
            .atMost(5, TimeUnit.SECONDS)
            .until {
                val schedule = concertScheduleJpaRepository.findById(1L).get()
                val outbox =
                    outboxJpaRepository.findByEventTypeAndKey(
                        eventType = "RESERVATION_CREATED",
                        key = createReservationInfo.reservationId.toString(),
                    )
                schedule.availableSeats == 4 &&
                    outbox.status == OutboxStatus.PUBLISHED
            }

        // then
        assertNotNull(createReservationInfo)
        assertEquals(true, createReservationInfo.success)
        assertEquals(1L, createReservationInfo.reservationId)

        assertEquals(SeatStatus.UNAVAILABLE, seatJpaRepository.findById(command.seatId).get().status)
        assertEquals(4, concertScheduleJpaRepository.findById(command.scheduleId).get().availableSeats)
        assertEquals(ReservationStatus.PENDING, reservationJpaRepository.findById(1L).get().status)

        val outbox =
            outboxJpaRepository.findByEventTypeAndKey(
                eventType = "RESERVATION_CREATED",
                key = createReservationInfo.reservationId.toString(),
            )
        assertEquals("PUBLISHED", outbox.status.name)

        val consumer = kafkaListenerContainerFactory.consumerFactory.createConsumer()
        consumer.subscribe(listOf("concert.reservation.created"))
        val records = consumer.poll(Duration.ofSeconds(5))
        assertTrue(records.count() > 0)
        assertEquals("1", records.first().key())
        consumer.close()
    }

    @Test
    fun `should throw exception when retrieving available reservation dates for non-existing concert`() {
        // given
        val nonExistingConcertId = 999L // 존재하지 않는 콘서트 ID
        val token = "123e4567-e89b-12d3-a456-426614174000"

        // when & then
        val exception =
            assertThrows<CoreException> {
                concertFacade.getReservationAvailableDates(token, nonExistingConcertId)
            }

        assertEquals("No concert found for the given ID.", exception.message)
    }

    @Test
    fun `should throw exception when creating reservation for non-existing schedule`() {
        // given
        val userId = 1L
        val nonExistingScheduleId = 999L // 존재하지 않는 일정 ID
        val seatId = 1L
        val token = "123e4567-e89b-12d3-a456-426614174000"

        val command =
            ReservationCommand(
                userId = userId,
                scheduleId = nonExistingScheduleId,
                seatId = seatId,
                token = token,
            )

        // when & then
        val exception =
            assertThrows<CoreException> {
                concertFacade.createReservation(command)
            }

        assertEquals("Token does not belong to the concert schedule.", exception.message)
    }

    @Test
    fun `should throw exception when all seats are sold out`() {
        // given
        val userId = 1L
        val scheduleId = schedule2.id // 예약이 모두 끝난 일정 ID
        val seatId = 6L
        val token = "123e4567-e89b-12d3-a456-426614174001"

        val command =
            ReservationCommand(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId,
                token = token,
            )

        // when & then
        val exception =
            assertThrows<CoreException> {
                concertFacade.createReservation(command)
            }

        assertEquals("The concert schedule is sold out.", exception.message)
    }

    @Test
    fun `should throw exception when seat is already reserved on second attempt`() {
        // given
        val userId = 1L
        val scheduleId = schedule1.id
        val seatId = 1L
        val token = "123e4567-e89b-12d3-a456-426614174000"

        val command =
            ReservationCommand(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId,
                token = token,
            )

        // when - 첫 번째 예약 성공
        concertFacade.createReservation(command)

        // then - 두 번째 예약 시도에서 예외 발생
        val exception =
            assertThrows<CoreException> {
                concertFacade.createReservation(command)
            }

        assertEquals("The seat is not available for reservation.", exception.message)
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

    @Test
    fun `should reserve seat for only one user when multiple requests are made concurrently`() {
        // Given
        val seatId = 1L
        val scheduleId = 1L
        for (i in 6..5000) {
            userJpaRepository.save(
                User(
                    name = "User$i",
                    email = "user$i@test.com",
                    balance = 100000.toBigDecimal(),
                ),
            )
        }
        val userIds = (1L..5000L).toList()
        val executor: ExecutorService = Executors.newFixedThreadPool(100)

        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        val responseTimes = ConcurrentLinkedQueue<Long>()

        val startLatch = CountDownLatch(1)
        val endLatch = CountDownLatch(userIds.size)

        // When
        val tasks =
            userIds.map { userId ->
                Callable {
                    startLatch.await()
                    val startTime = System.nanoTime()
                    try {
                        concertFacade.createReservation(
                            ReservationCommand(
                                userId = userId,
                                scheduleId = scheduleId,
                                seatId = seatId,
                                token = "123e4567-e89b-12d3-a456-426614174000",
                            ),
                        )
                        successCount.incrementAndGet()
                    } catch (e: CoreException) {
//                        println("CoreException: ${e.javaClass.simpleName} - ${e.message}")
                        failureCount.incrementAndGet()
                    } catch (e: ObjectOptimisticLockingFailureException) {
//                        println("OptimisticLockException: ${e.javaClass.simpleName} - ${e.message}")
                        failureCount.incrementAndGet()
                    } catch (e: Exception) {
//                        println("Unexpected exception: ${e.javaClass.simpleName} - ${e.message}")
                        failureCount.incrementAndGet()
                    } finally {
                        val endTime = System.nanoTime()
                        responseTimes.add(endTime - startTime)
                        endLatch.countDown()
                    }
                }
            }

        tasks.forEach { executor.submit(it) }
        startLatch.countDown()
        endLatch.await()
        executor.shutdown()

        // Then
        assertEquals(1, successCount.get())
        assertEquals(userIds.size - 1, failureCount.get())

        // 응답 시간 분석
        val responseTimeList = responseTimes.toList()
        val fastestResponse = responseTimeList.minOrNull()?.let { it / 1_000_000 } ?: 0
        val slowestResponse = responseTimeList.maxOrNull()?.let { it / 1_000_000 } ?: 0
        val averageResponse = if (responseTimeList.isNotEmpty()) responseTimeList.average() / 1_000_000 else 0.0

        println("Fastest Response Time: ${fastestResponse}ms")
        println("Slowest Response Time: ${slowestResponse}ms")
        println("Average Response Time: ${averageResponse}ms")
    }
}
