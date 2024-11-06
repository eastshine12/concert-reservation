package hhplus.concertreservation.domain.waitingQueue.dto.info

import hhplus.concertreservation.domain.common.enums.QueueStatus
import java.time.LocalDateTime

data class TokenInfo(
    val scheduleId: Long,
    val token: String,
    val status: QueueStatus,
    val expiresAt: LocalDateTime?,
)
