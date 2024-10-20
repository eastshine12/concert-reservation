package hhplus.concertreservation.application.payment

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.payment.dto.command.PaymentCommand
import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.concert.service.ReservationService
import hhplus.concertreservation.domain.payment.Payment
import hhplus.concertreservation.domain.payment.PaymentService
import hhplus.concertreservation.domain.payment.toPaymentInfo
import hhplus.concertreservation.domain.user.service.UserService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val waitingQueueService: WaitingQueueService,
    private val userService: UserService,
    private val reservationService: ReservationService,
    private val paymentService: PaymentService,
    private val concertService: ConcertService,
) {

    @Transactional
    fun processPayment(command: PaymentCommand): PaymentInfo {
        waitingQueueService.validateTokenState(command.token)
        val reservation: Reservation = reservationService.confirmReservation(command.reservationId)
        val seat: Seat = concertService.getSeatById(reservation.seatId)
        userService.updateUserBalance(command.userId, seat.price, PointTransactionType.USE)
        val payment: Payment = paymentService.savePayment(command.userId, command.reservationId, seat.price)
        waitingQueueService.expireToken(command.token)
        return payment.toPaymentInfo()
    }

    fun getPaymentsByUserId(userId: Long): List<PaymentInfo> {
        val payments: List<Payment> = paymentService.getPaymentsByUserId(userId)
        return payments.map { it.toPaymentInfo() }
    }
}
