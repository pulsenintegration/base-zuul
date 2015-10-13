package com.confluex.error.web.message.impl

import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import com.confluex.error.web.message.HttpErrorMessage
import com.confluex.error.web.message.HttpErrorMessageConverter

import javax.servlet.http.HttpServletRequest

/**
 * Base implementation which provides basic properties available from most requests.
 */
@Slf4j
abstract class BaseConverter<T extends Throwable> implements HttpErrorMessageConverter<T> {

  HttpErrorMessage convert(T error, HttpServletRequest request) {
    log.debug("Converting error to HttpErrorMessage", error)
    def message = new HttpErrorMessage()
    message.messages = [error.message]
    message.statusCode = statusCode

    message.user = request.userPrincipal?.toString()
    message.requestUri = request.requestURI
    populate(error, request, message)
    if (message.isClientError()) {
      log.warn("Client error: {}", message)
    }
    else {
      message.stackTrace = ExceptionUtils.getStackTrace(error)
      log.error("Server error: {}", message)
    }
    return message
  }

  /**
   * Template method for populating fields specific to the error type
   */
  abstract void populate(T error, HttpServletRequest request, HttpErrorMessage message)

  /**
   * Which http status code to use
   */
  abstract Integer getStatusCode()

}
