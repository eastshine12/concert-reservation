package hhplus.concertreservation.domain.user.entity

import hhplus.concertreservation.domain.user.exception.InvalidBalanceAmountException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UserTest {

    @Test
    fun `must throw exception when charge amount is non-positive`() {
        // given
        val user = User(name = "홍길동", email = "hong@example.com", balance = BigDecimal("100.00"))

        // when / then
        assertThrows<InvalidBalanceAmountException> {
            user.charge(BigDecimal("-10.00"))
        }
    }

    @Test
    fun `must throw exception when usage amount is non-positive`() {
        // given
        val user = User(name = "홍길동", email = "hong@example.com", balance = BigDecimal("100.00"))

        // when / then
        assertThrows<InvalidBalanceAmountException> {
            user.use(BigDecimal("0.00"))
        }
    }

    @Test
    fun `must throw exception when balance is insufficient`() {
        // given
        val user = User(name = "홍길동", email = "hong@example.com", balance = BigDecimal("50.00"))

        // when / then
        assertThrows<InvalidBalanceAmountException> {
            user.use(BigDecimal("60.00"))
        }
    }
}
