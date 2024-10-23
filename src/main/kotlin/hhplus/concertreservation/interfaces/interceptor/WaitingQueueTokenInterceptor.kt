package hhplus.concertreservation.interfaces.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class WaitingQueueTokenInterceptor(
    private val waitingQueueService: WaitingQueueService,
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val token =
            request.getHeader("Queue-Token") ?: throw CoreException(
                errorType = ErrorType.INVALID_TOKEN,
                message = "Token is missing from the request header.",
            )
        val scheduleId: Long? = extractScheduleIdOrNullFromRequest(request)
        waitingQueueService.validateTokenState(token, scheduleId)

        return true
    }

    private fun extractScheduleIdOrNullFromRequest(request: HttpServletRequest): Long? {
        val scheduleIdFromPath: Long? =
            Regex("/concerts/\\d+/schedules/(\\d+)")
                .find(request.requestURI)
                ?.groups?.get(1)
                ?.value
                ?.toLongOrNull()

        val scheduleIdFromBody: Long? =
            request.inputStream.use { inputStream ->
                objectMapper.readTree(inputStream).takeIf { it.has("scheduleId") }?.get("scheduleId")?.asLong()
            }

        return scheduleIdFromPath ?: scheduleIdFromBody
    }
}
