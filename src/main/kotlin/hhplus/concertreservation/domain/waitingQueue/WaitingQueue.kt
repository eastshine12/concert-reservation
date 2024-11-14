package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import java.time.LocalDateTime

data class WaitingQueue(
    val token: String,
    val scheduleId: Long,
    var position: Int = 0,
    val status: QueueStatus,
    val expiresAt: LocalDateTime? = null,
)
