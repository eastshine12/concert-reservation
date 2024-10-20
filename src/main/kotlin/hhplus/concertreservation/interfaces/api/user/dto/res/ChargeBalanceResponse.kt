package hhplus.concertreservation.interfaces.api.user.dto.res

import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo

data class ChargeBalanceResponse(
    val status: String,
) {
    companion object {
        fun fromInfo(updateBalanceInfo: UpdateBalanceInfo): ChargeBalanceResponse {
            return ChargeBalanceResponse(
                status = if (updateBalanceInfo.success) "success" else "failure",
            )
        }
    }
}
