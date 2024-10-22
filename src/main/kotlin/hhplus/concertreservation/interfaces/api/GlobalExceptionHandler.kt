package hhplus.concertreservation.interfaces.api

import hhplus.concertreservation.domain.common.error.ErrorCode
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.error.LogLevel.*
import hhplus.concertreservation.domain.common.exception.CoreException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(ex: CoreException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorType: ErrorType = ex.errorType
        val detailsMessage = ex.details?.toString() ?: "No details available"

        when (errorType.logLevel) {
            INFO -> log.info("Exception: [${errorType.code}] ${ex.message}. Details: $detailsMessage")
            WARN -> log.warn("Exception: [${errorType.code}] ${ex.message}. Details: $detailsMessage")
            ERROR -> log.error("Exception: [${errorType.code}] ${ex.message}. Details: $detailsMessage", ex)
        }

        val errorResponse = ErrorResponse(
            status = getHttpStatusByErrorCode(errorType.code).value(),
            errorCode = errorType.code.name,
            message = ex.message.toString(),
            path = request.servletPath,
            details = ex.details,
        )

        return ResponseEntity.status(getHttpStatusByErrorCode(errorType.code)).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error occurred: ${ex.message}", ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            errorCode = "SYSTEM_ERROR",
            message = "An unexpected error occurred.",
            path = request.servletPath
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    private fun getHttpStatusByErrorCode(errorCode: ErrorCode): HttpStatus {
        return when (errorCode) {
            ErrorCode.VALIDATION_ERROR -> HttpStatus.BAD_REQUEST
            ErrorCode.SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
            ErrorCode.AUTHENTICATION_ERROR -> HttpStatus.UNAUTHORIZED
            ErrorCode.AUTHORIZATION_ERROR -> HttpStatus.FORBIDDEN
            ErrorCode.DATABASE_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
            ErrorCode.BUSINESS_ERROR -> HttpStatus.CONFLICT
            ErrorCode.QUEUE_ERROR -> HttpStatus.SERVICE_UNAVAILABLE
        }
    }
}

data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val status: Int,
    val errorCode: String,
    val message: String,
    val path: String? = null,
    val details: Any? = null,
)
