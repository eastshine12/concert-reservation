package hhplus.concertreservation.interfaces.api.user.dto.req

import hhplus.concertreservation.application.user.dto.command.ChargeBalanceCommand
import java.math.BigDecimal

data class ChargeBalanceRequest(
    val amount: BigDecimal,
) {
    fun toCommand(token: String, userId: Long): ChargeBalanceCommand {
        return ChargeBalanceCommand(
            token = token,
            userId = userId,
            amount = amount,
        )
    }
}
