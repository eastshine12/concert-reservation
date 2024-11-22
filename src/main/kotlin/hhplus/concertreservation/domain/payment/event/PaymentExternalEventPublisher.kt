package hhplus.concertreservation.domain.payment.event

interface PaymentExternalEventPublisher {
    fun publish(
        topic: String,
        key: String,
        payload: Any,
    )
}
