package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.QueueStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class WaitingQueue(
    var scheduleId: Long,
    var token: String,
    @Enumerated(EnumType.STRING)
    var status: QueueStatus,
    var queuePosition: Int,
    var expiresAt: LocalDateTime?,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity() {

    fun expire() {
        status = QueueStatus.EXPIRED
    }

    fun activate(expireMinutes: Long) {
        this.status = QueueStatus.ACTIVE
        this.expiresAt = LocalDateTime.now().plusMinutes(expireMinutes)
    }
}
