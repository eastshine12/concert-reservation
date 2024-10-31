package hhplus.concertreservation.application.payment

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.concert.service.ReservationService
import hhplus.concertreservation.domain.payment.Payment
import hhplus.concertreservation.domain.payment.PaymentService
import hhplus.concertreservation.domain.payment.dto.command.PaymentCommand
import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
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
        return runCatching {
            val reservationInfo: ReservationInfo = reservationService.confirmReservation(command.reservationId)
            val seatInfo: SeatInfo = concertService.verifyAndGetSeatInfo(reservationInfo.seatId)
            userService.updateUserBalance(command.userId, seatInfo.price, PointTransactionType.USE)
            waitingQueueService.expireToken(command.token)
            paymentService.savePayment(command.userId, command.reservationId, seatInfo.price)
        }.getOrElse { ex ->
            throw CoreException(
                errorType = ErrorType.PAYMENT_FAILED,
                details = {
                    "cause" to ex.message
                },
            )
        }
    }

    fun getPaymentsByUserId(userId: Long): List<PaymentInfo> {
        val payments: List<Payment> = paymentService.getPaymentsByUserId(userId)
        return payments.map { it.toPaymentInfo() }
    }
}
