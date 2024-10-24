package hhplus.concertreservation.application.user.dto.command

import java.math.BigDecimal

data class ChargeBalanceCommand(
    val token: String,
    val userId: Long,
    val amount: BigDecimal
)
