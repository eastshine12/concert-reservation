package hhplus.concertreservation.interfaces.interceptor

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
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val token = request.getHeader("Queue-Token")
            ?: throw CoreException(
                errorType = ErrorType.INVALID_TOKEN,
                message = "Token is missing from the request header.",
            )
        val scheduleId: Long? = extractScheduleIdOrNullFromRequest(request.requestURI)
        waitingQueueService.validateTokenState(token, scheduleId)

        return true
    }

    private fun extractScheduleIdOrNullFromRequest(requestURI: String): Long? {
        val scheduleIdFromPath: Long? =
            Regex("/concerts/\\d+/schedules/(\\d+)")
                .find(requestURI)
                ?.groups?.get(1)
                ?.value
                ?.toLongOrNull()

        return scheduleIdFromPath
    }
}
