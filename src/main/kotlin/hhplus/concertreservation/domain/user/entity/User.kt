package hhplus.concertreservation.domain.user.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "users")
@Entity
class User(
    name: String,
    email: String,
    balance: BigDecimal,
    @Version
    var version: Long = 0L,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity() {
    var name: String = name
        protected set

    var email: String = email
        protected set

    var balance: BigDecimal = balance
        protected set

    fun charge(amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw CoreException(
                errorType = ErrorType.INVALID_BALANCE_AMOUNT,
                message = "Charge amount must be positive",
            )
        }
        balance = balance.add(amount)
    }

    fun use(amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw CoreException(
                errorType = ErrorType.INVALID_BALANCE_AMOUNT,
                message = "Usage amount must be positive",
            )
        }
        if (balance < amount) {
            throw CoreException(
                errorType = ErrorType.INSUFFICIENT_BALANCE,
            )
        }
        balance = balance.subtract(amount)
    }
}
