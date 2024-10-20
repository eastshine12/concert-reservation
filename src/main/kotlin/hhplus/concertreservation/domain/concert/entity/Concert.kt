package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Concert(
    title: String,
    duration: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {

    var title: String = title
        protected set

    var duration: Int = duration
        protected set
}
