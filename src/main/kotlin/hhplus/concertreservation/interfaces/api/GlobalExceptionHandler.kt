package hhplus.concertreservation.interfaces.api

import hhplus.concertreservation.domain.user.exception.UserNotFoundException
import jakarta.persistence.OptimisticLockException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(OptimisticLockException::class)
    fun handleOptimisticLockException(e: OptimisticLockException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Concurrency conflict occurred. Please try again.")
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }
}
