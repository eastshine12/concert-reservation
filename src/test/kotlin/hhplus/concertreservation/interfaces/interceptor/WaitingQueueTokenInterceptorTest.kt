package hhplus.concertreservation.interfaces.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.Test

@SpringBootTest
class WaitingQueueTokenInterceptorTest {
    private val waitingQueueService = mockk<WaitingQueueService>()
    private val objectMapper = ObjectMapper()
    private val waitingQueueTokenInterceptor =
        WaitingQueueTokenInterceptor(waitingQueueService, objectMapper)

    @Test
    fun `should validate token from request with scheduleId`() {
        // given
        val request =
            MockHttpServletRequest().apply {
                requestURI = "/concerts/1/schedules/2/seats"
                addHeader("Queue-Token", "test-token")
            }
        val response = MockHttpServletResponse()
        val handler = Any()

        every { waitingQueueService.validateTokenState("test-token", 2L) } returns mockk<WaitingQueue>()

        // when
        val result = waitingQueueTokenInterceptor.preHandle(request, response, handler)

        // then
        assertTrue(result)
        verify { waitingQueueService.validateTokenState("test-token", 2L) }
    }

    @Test
    fun `should validate token from request without scheduleId`() {
        // given
        val request =
            MockHttpServletRequest().apply {
                requestURI = "/api/payments"
                addHeader("Queue-Token", "test-token")
            }
        val response = MockHttpServletResponse()
        val handler = Any()

        every { waitingQueueService.validateTokenState("test-token", null) } returns mockk<WaitingQueue>()

        // when
        val result = waitingQueueTokenInterceptor.preHandle(request, response, handler)

        // then
        assertTrue(result)
        verify { waitingQueueService.validateTokenState("test-token", null) }
    }

    @Test
    fun `should throw exception when token is missing`() {
        // given
        val request =
            MockHttpServletRequest().apply {
                requestURI = "/concerts/1/schedules/2/seats"
            }
        val response = MockHttpServletResponse()
        val handler = Any()

        // when / then
        val exception =
            assertThrows<CoreException> {
                waitingQueueTokenInterceptor.preHandle(request, response, handler)
            }
        assertEquals(ErrorType.INVALID_TOKEN, exception.errorType)
    }
}
