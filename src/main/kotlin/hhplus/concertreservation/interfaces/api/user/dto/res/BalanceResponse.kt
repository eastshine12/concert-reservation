package hhplus.concertreservation.interfaces.api.user.dto.res

import java.math.BigDecimal

data class BalanceResponse(
    val balance: BigDecimal,
) {
    companion object {
        fun fromInfo(amount: BigDecimal): BalanceResponse {
            return BalanceResponse(
                balance = amount,
            )
        }
    }
}
