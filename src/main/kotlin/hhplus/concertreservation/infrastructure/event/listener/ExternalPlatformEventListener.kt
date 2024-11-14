package hhplus.concertreservation.infrastructure.event.listener

import hhplus.concertreservation.domain.concert.event.ReservationCreatedEvent
import hhplus.concertreservation.infrastructure.api.DataPlatformApiClient
import hhplus.concertreservation.infrastructure.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ExternalPlatformEventListener(
    private val dataPlatformApiClient: DataPlatformApiClient
): EventListener<ReservationCreatedEvent> {
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    override fun handle(event: ReservationCreatedEvent) {
        dataPlatformApiClient.sendReservation()
    }
}
