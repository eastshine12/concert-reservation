package hhplus.concertreservation.application.user

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.user.dto.command.ChargeBalanceCommand
import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.service.UserService
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class UserFacade(
    private val userService: UserService,
) {
    fun chargeBalance(command: ChargeBalanceCommand): UpdateBalanceInfo {
        return userService.updateUserBalance(command.userId, command.amount, PointTransactionType.CHARGE)
    }

    fun getUserBalance(
        token: String,
        userId: Long,
    ): BigDecimal {
        return userService.getUserBalance(userId)
    }
}
