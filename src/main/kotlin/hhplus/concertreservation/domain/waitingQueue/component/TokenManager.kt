package hhplus.concertreservation.domain.waitingQueue.component

import hhplus.concertreservation.domain.waitingQueue.exception.InvalidTokenException
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
            throw InvalidTokenException("토큰 형식이 유효하지 않습니다: $token")
        }
    }
}
