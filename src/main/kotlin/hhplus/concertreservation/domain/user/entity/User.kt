package hhplus.concertreservation.domain.user.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.user.exception.InvalidBalanceAmountException
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "users")
@Entity
class User(
    var name: String,
    var email: String,
    var balance: BigDecimal,
    @Version
    var version: Long? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity() {
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
            throw InvalidBalanceAmountException("Insufficient balance")
        }
        balance = balance.subtract(amount)
    }
}
