package hhplus.concertreservation.domain.payment

interface PaymentRepository {
    fun save(payment: Payment): Payment

    fun findAllByUserId(userId: Long): List<Payment>
}
