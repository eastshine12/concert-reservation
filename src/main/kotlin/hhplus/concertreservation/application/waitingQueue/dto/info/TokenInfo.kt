package hhplus.concertreservation.application.waitingQueue.dto.info

import hhplus.concertreservation.domain.common.enums.QueueStatus
import java.time.LocalDateTime

data class TokenInfo(
    val queueId: Long,
    val scheduleId: Long,
    val token: String,
    val status: QueueStatus,
    val queuePosition: Int,
    val expiresAt: LocalDateTime?,
)
