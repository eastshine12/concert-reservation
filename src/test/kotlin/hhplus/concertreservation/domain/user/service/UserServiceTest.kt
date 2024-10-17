package hhplus.concertreservation.domain.user.service

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.user.component.BalanceManager
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.exception.UserNotFoundException
import hhplus.concertreservation.domain.user.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val balanceManager = mockk<BalanceManager>()
    private val userService = UserService(userRepository, balanceManager)

    @Test
    fun `should return user by id`() {
        // given
        val userId = 1L
        val user = mockk<User>()
        every { userRepository.findByIdOrNull(userId) } returns user

        // when
        val result = userService.getByUserId(userId)

        // then
        assertEquals(user, result)
    }

    @Test
    fun `must throw exception when user not found`() {
        // given
        val userId = 1L
        every { userRepository.findByIdOrNull(userId) } returns null

        // when / then
        assertThrows<UserNotFoundException> {
            userService.getByUserId(userId)
        }
    }

    @Test
    fun `should charge user balance successfully`() {
        // given
        val userId = 1L
        val amount = BigDecimal("50.00")
        val user = mockk<User>(relaxed = true)
        val balanceHistory = mockk<BalanceHistory>()
        every { userRepository.findByIdOrNull(userId) } returns user
        every { balanceManager.saveBalanceHistory(userId, amount, PointTransactionType.CHARGE) } returns balanceHistory

        // when
        val result = userService.chargeUserBalance(userId, amount)

        // then
        assertEquals(balanceHistory, result)
    }

    @Test
    fun `should deduct user balance successfully`() {
        // given
        val userId = 1L
        val amount = BigDecimal("30.00")
        val user = mockk<User>(relaxed = true)
        val balanceHistory = mockk<BalanceHistory>()
        every { userRepository.findByIdOrNull(userId) } returns user
        every { balanceManager.saveBalanceHistory(userId, amount, PointTransactionType.USE) } returns balanceHistory

        // when
        val result = userService.deductUserBalance(userId, amount)

        // then
        assertEquals(balanceHistory, result)
    }
}
