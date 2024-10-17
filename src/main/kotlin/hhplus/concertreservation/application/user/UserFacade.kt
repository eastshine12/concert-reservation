package hhplus.concertreservation.application.user

import hhplus.concertreservation.application.user.dto.command.ChargeBalanceCommand
import hhplus.concertreservation.application.user.dto.info.ChargeBalanceInfo
import hhplus.concertreservation.application.user.mapper.BalanceMapper
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.service.UserService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class UserFacade(
    private val waitingQueueService: WaitingQueueService,
    private val userService: UserService,
    private val balanceMapper: BalanceMapper,
) {
    fun chargeBalance(command: ChargeBalanceCommand): ChargeBalanceInfo {
        waitingQueueService.validateTokenState(command.token)
        val chargeUserBalance: BalanceHistory = userService.chargeUserBalance(command.userId, command.amount)
        return balanceMapper.toChargeBalanceInfo(chargeUserBalance)
    }

    fun getUserBalance(token: String, userId: Long): BigDecimal {
        waitingQueueService.validateTokenState(token)
        return userService.getUserBalance(userId)
    }
}