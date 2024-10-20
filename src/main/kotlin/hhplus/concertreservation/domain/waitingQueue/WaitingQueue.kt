package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.QueueStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class WaitingQueue(
    val scheduleId: Long,
    val token: String,
    status: QueueStatus,
    val queuePosition: Int,
    expiresAt: LocalDateTime?,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    var status: QueueStatus = status
        protected set

    var expiresAt: LocalDateTime? = expiresAt
        protected set

    fun expire() {
        status = QueueStatus.EXPIRED
    }

    fun activate(expireMinutes: Long) {
        this.status = QueueStatus.ACTIVE
        this.expiresAt = LocalDateTime.now().plusMinutes(expireMinutes)
    }
}
