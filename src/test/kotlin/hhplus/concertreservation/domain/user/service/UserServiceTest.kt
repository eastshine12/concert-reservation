package hhplus.concertreservation.domain.user.service

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.repository.BalanceHistoryRepository
import hhplus.concertreservation.domain.user.repository.UserRepository
import hhplus.concertreservation.domain.user.toUpdateBalanceInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class UserServiceTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val balanceHistoryRepository = mockk<BalanceHistoryRepository>(relaxed = true)
    private val userService = UserService(userRepository, balanceHistoryRepository)

    @Test
    fun `should return user by id`() {
        // given
        val userId = 1L
        val user = mockk<User>()
        every { userRepository.findByIdOrNull(userId) } returns user

        // when
        val result = userService.checkUserExists(userId)

        // then
        assertEquals(user, result)
    }

    @Test
    fun `must throw exception when user not found`() {
        // given
        val userId = 1L
        every { userRepository.findByIdOrNull(userId) } returns null

        // when / then
        assertThrows<CoreException> {
            userService.checkUserExists(userId)
        }
    }

    @Test
    fun `should charge user balance successfully`() {
        // given
        val userId = 1L
        val amount = BigDecimal("50.00")
        val type = PointTransactionType.CHARGE
        val user = mockk<User>(relaxed = true)
        val balanceHistory = mockk<BalanceHistory>(relaxed = true)
        val updateBalanceInfo = UpdateBalanceInfo(success = true, balanceHistoryId = 1L)
        every { userRepository.findByIdOrNull(userId) } returns user
        every { balanceHistoryRepository.save(balanceHistory) } returns balanceHistory
        every { balanceHistory.toUpdateBalanceInfo(success = true) } returns updateBalanceInfo

        // when
        val result = userService.updateUserBalance(userId, amount, type)

        // then
        assertEquals(updateBalanceInfo, result)
    }

    @Test
    fun `should deduct user balance successfully`() {
        // given
        val userId = 1L
        val amount = BigDecimal("30.00")
        val user = mockk<User>(relaxed = true)
        val balanceHistory = mockk<BalanceHistory>()
        val updateBalanceInfo = mockk<UpdateBalanceInfo>()
        every { userRepository.findByIdOrNull(userId) } returns user
        every { balanceHistoryRepository.save(balanceHistory) } returns balanceHistory
        every { balanceHistory.toUpdateBalanceInfo(success = true) } returns updateBalanceInfo

        // when
        val result = userService.updateUserBalance(userId, amount, PointTransactionType.USE)

        // then
        assertEquals(updateBalanceInfo, result)
    }
}
