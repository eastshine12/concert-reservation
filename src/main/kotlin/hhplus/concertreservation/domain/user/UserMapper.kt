package hhplus.concertreservation.domain.user

import hhplus.concertreservation.application.user.dto.info.ChargeBalanceInfo
import hhplus.concertreservation.domain.user.entity.BalanceHistory

fun BalanceHistory.toChargeBalanceInfo(success: Boolean): ChargeBalanceInfo {
    return ChargeBalanceInfo(
        success = success,
        balanceHistoryId = this.id,
    )
}