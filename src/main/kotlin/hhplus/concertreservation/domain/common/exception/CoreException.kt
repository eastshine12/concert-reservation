package hhplus.concertreservation.domain.common.exception

import hhplus.concertreservation.domain.common.error.ErrorType

class CoreException(
    val errorType: ErrorType,
    val details: Any? = null,
    message: String = errorType.message,
) : RuntimeException(message)
