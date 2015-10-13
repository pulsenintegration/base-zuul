package com.confluex.error.web.message.impl

import groovy.util.logging.Slf4j
import com.confluex.error.web.message.HttpErrorMessage

import javax.servlet.http.HttpServletRequest

import static com.confluex.error.web.message.ErrorCodes.*

/**
 * Fallback which supports all throwables
 */
@Slf4j
class CatchAllConverter extends BaseConverter<Throwable> {

  /**
   * Default: {@value}
   */
  Integer statusCode = SERVER_ERROR_UNHANDLED

  /**
   * Value to use for HttpErrorMessage.message
   *
   * Default: {@value}
   */
  String errorMessage = "Unhandled server error"

  void populate(Throwable ex, HttpServletRequest request, HttpErrorMessage message) {
    message.messages = [errorMessage, ex.message]
  }

}
