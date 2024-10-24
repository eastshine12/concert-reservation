package hhplus.concertreservation.application.waitingQueue.dto.info

import hhplus.concertreservation.domain.common.enums.QueueStatus
import java.time.LocalDateTime

data class WaitingQueueInfo(
    val scheduleId: Long,
    val status: QueueStatus,
    val remainingPosition: Int,
    val expiresAt: LocalDateTime?
)
