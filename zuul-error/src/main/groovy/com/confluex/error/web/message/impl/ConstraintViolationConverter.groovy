package com.confluex.error.web.message.impl

import groovy.util.logging.Slf4j
import com.confluex.error.web.message.HttpErrorMessage

import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

import static com.confluex.error.web.message.ErrorCodes.*

/**
 * Collects constraintViolation message list as messages
 */
@Slf4j
class ConstraintViolationConverter extends BaseConverter<ConstraintViolationException> {

  /**
   * Default: {@value}
   */
  Integer statusCode = CLIENT_ERROR_INVALID_ENTITY

  void populate(ConstraintViolationException ex, HttpServletRequest request, HttpErrorMessage message) {
    message.messages = ex.constraintViolations.collect { it.message }
  }

}
