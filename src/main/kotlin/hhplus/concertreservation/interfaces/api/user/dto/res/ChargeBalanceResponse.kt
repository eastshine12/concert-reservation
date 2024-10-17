package hhplus.concertreservation.interfaces.api.user.dto.res

import hhplus.concertreservation.application.user.dto.info.ChargeBalanceInfo

data class ChargeBalanceResponse(
    val status: String,
) {
    companion object {
        fun fromInfo(chargeBalanceInfo: ChargeBalanceInfo): ChargeBalanceResponse {
            return ChargeBalanceResponse(
                status = if (chargeBalanceInfo.success) "success" else "failure"
            )
        }
    }
}
