package hhplus.concertreservation.domain.user.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.user.exception.InsufficientBalanceException
import hhplus.concertreservation.domain.user.exception.InvalidBalanceAmountException
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "users")
@Entity
class User(
    name: String,
    email: String,
    balance: BigDecimal,
    @Version
    var version: Long? = null,
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
            throw InvalidBalanceAmountException("Charge amount must be positive")
        }
        balance = balance.add(amount)
    }

    fun use(amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidBalanceAmountException("Usage amount must be positive")
        }
        if (balance < amount) {
            throw InsufficientBalanceException("Insufficient balance")
        }
        balance = balance.subtract(amount)
    }
}
