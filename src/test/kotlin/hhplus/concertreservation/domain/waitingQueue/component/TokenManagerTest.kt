package hhplus.concertreservation.domain.waitingQueue.component

import hhplus.concertreservation.domain.waitingQueue.exception.InvalidTokenException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TokenManagerTest {
    private val tokenManager = TokenManager()

    @Test
    fun `should validate and return token successfully`() {
        // given
        val validToken = tokenManager.generateToken()

        // when
        val result = tokenManager.validateAndGetToken(validToken)

        // then
        assertEquals(validToken, result)
    }

    @Test
    fun `must throw InvalidTokenException for invalid token format`() {
        // given
        val invalidToken = "invalid-token-format"

        // when, then
        assertThrows<InvalidTokenException> {
            tokenManager.validateAndGetToken(invalidToken)
        }
    }
}
