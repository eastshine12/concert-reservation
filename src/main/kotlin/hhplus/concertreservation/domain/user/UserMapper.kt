package hhplus.concertreservation.domain.user

import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.entity.BalanceHistory

fun BalanceHistory.toUpdateBalanceInfo(success: Boolean): UpdateBalanceInfo {
    return UpdateBalanceInfo(
        success = success,
        balanceHistoryId = this.id,
    )
}