package hhplus.concertreservation.interfaces.api.waitingQueue.dto.res

import hhplus.concertreservation.application.waitingQueue.dto.info.WaitingQueueInfo

data class QueueResponse(
    val status: String,
    val remainingPosition: String?,
) {
    companion object {
        fun fromInfo(info: WaitingQueueInfo): QueueResponse {
            return QueueResponse(
                status = info.status.name,
                remainingPosition = info.remainingPosition.toString()
            )
        }
    }
}
