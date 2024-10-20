package hhplus.concertreservation.application.user

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.user.dto.command.ChargeBalanceCommand
import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.service.UserService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class UserFacade(
    private val waitingQueueService: WaitingQueueService,
    private val userService: UserService,
) {
    fun chargeBalance(command: ChargeBalanceCommand): UpdateBalanceInfo {
        waitingQueueService.validateTokenState(command.token)
        return userService.updateUserBalance(command.userId, command.amount, PointTransactionType.CHARGE)
    }

    fun getUserBalance(
        token: String,
        userId: Long,
    ): BigDecimal {
        waitingQueueService.validateTokenState(token)
        return userService.getUserBalance(userId)
    }
}
