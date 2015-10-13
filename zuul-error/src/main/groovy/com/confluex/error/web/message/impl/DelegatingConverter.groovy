package com.confluex.error.web.message.impl

import org.apache.commons.lang.exception.ExceptionUtils
import com.confluex.error.web.message.HttpErrorMessage
import com.confluex.error.web.message.HttpErrorMessageConverter

import javax.servlet.http.HttpServletRequest
import groovy.util.logging.Slf4j

/**
 * Chains HttpErrorMessageConverters. Delegates to first match that supports any of the exception types
 * in the exception chain.
 */
@Slf4j
class DelegatingConverter implements HttpErrorMessageConverter<Throwable> {
  List<HttpErrorMessageConverter> converters = []

  HttpErrorMessage convert(Throwable error, HttpServletRequest request) {
    log.debug("Finding a converter for error {}", error.class)
    for (HttpErrorMessageConverter c : converters) {
      def supportedType = getAssignedType(c)
      def supportedThrowable = findSupportedError(error, supportedType)
      if (supportedType && supportedThrowable) {
        log.debug("Matched error converter:{} to type: {}", c.class, supportedType)
        return c.convert(supportedThrowable, request)
      }
    }
    log.warn("No error converter found for error: {}", error.class)
    throw error
  }

  protected Class getAssignedType(HttpErrorMessageConverter converter) {
    return converter.class.genericSuperclass.actualTypeArguments.first()
  }

  protected Throwable findSupportedError(Throwable error, Class type) {
    ExceptionUtils.getThrowables(error).find { type.isAssignableFrom(it.class) }
  }

}
