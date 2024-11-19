package hhplus.concertreservation.domain.outbox

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.OutboxStatus
import jakarta.persistence.*

@Entity
@Table(name = "outbox")
class Outbox(
    val topic: String,
    val key: String,
    val payload: String,
    status: OutboxStatus = OutboxStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    var status: OutboxStatus = status
        protected set

    fun updateStatus(newStatus: OutboxStatus) {
        this.status = newStatus
    }
}
