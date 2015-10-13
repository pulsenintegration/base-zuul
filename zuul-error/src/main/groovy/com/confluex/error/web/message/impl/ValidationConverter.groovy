package com.confluex.error.web.message.impl

import groovy.util.logging.Slf4j
import com.confluex.error.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.web.servlet.support.RequestContextUtils

import javax.servlet.http.HttpServletRequest
import com.confluex.error.web.message.ErrorCodes
import com.confluex.error.web.message.HttpErrorMessage

/**
 * Default implementation of HttpErrorMessageConverter which knows how to convert DevNullException instances
 * as well as common Spring and javax.validation exceptions.
 */
@Slf4j
class ValidationConverter extends BaseConverter<ValidationException> {

  /**
   * Default: {@value}
   */
  Integer statusCode = ErrorCodes.CLIENT_ERROR_INVALID_ENTITY

  @Autowired(required = false)
  MessageSource messageSource

  void populate(ValidationException ex, HttpServletRequest request, HttpErrorMessage message) {
    //noinspection GroovyAssignabilityCheck
    message.messages = resolveMessages(ex.errors.globalErrors, request)
    ex.errors.fieldErrors?.groupBy { it.field }?.each { fieldName, errors ->
      //noinspection GroovyAssignabilityCheck
      message.fieldMessages[fieldName] = resolveMessages(errors, request)
    }
  }

  List<String> resolveMessages(List<MessageSourceResolvable> errors, HttpServletRequest request) {
    def locale = RequestContextUtils.getLocale(request)
    return errors?.collect { MessageSourceResolvable e ->
      messageSource?.getMessage(e, locale) ?: e.defaultMessage
    }
  }
}
