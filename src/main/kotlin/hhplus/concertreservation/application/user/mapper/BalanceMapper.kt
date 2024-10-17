package hhplus.concertreservation.application.user.mapper

import hhplus.concertreservation.application.user.dto.info.ChargeBalanceInfo
import hhplus.concertreservation.domain.payment.Payment
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import org.springframework.stereotype.Component

@Component
class BalanceMapper {
    fun toChargeBalanceInfo(balanceHistory: BalanceHistory): ChargeBalanceInfo {
        return ChargeBalanceInfo(
            success = true,
            balanceHistoryId = balanceHistory.id,
        )
    }
}
