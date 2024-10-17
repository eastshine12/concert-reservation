package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Concert(
    var title: String,
    var duration: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity()
