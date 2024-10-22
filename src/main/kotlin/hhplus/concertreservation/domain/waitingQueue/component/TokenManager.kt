package hhplus.concertreservation.domain.waitingQueue.component

import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenManager {
    fun generateToken(): String {
        return UUID.randomUUID().toString()
    }

    fun validateAndGetToken(token: String): String {
        return try {
            UUID.fromString(token)
            token
        } catch (e: IllegalArgumentException) {
            throw CoreException(
                errorType = ErrorType.INVALID_TOKEN,
            )
        }
    }
}
