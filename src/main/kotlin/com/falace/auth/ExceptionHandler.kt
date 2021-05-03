package com.falace.auth

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ExceptionHandler {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequests(ex: Throwable, response: HttpServletResponse) =
            response.sendError(BAD_REQUEST.value(), ex.message)

    @ExceptionHandler(IllegalAccessException::class)
    fun handleUnauthorizedAccess(ex: Throwable, response: HttpServletResponse) =
            response.sendError(UNAUTHORIZED.value(), ex.message)

    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(ex: Throwable, response: HttpServletResponse) {
        val errorCode = INTERNAL_SERVER_ERROR.value()
        logger.error("caught the following Exception:", ex)
        response.sendError(errorCode, ex.message)
    }

}