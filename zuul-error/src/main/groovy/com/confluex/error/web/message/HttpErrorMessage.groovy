package com.confluex.error.web.message


import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j

/**
 * Used by ErrorResolvers as a container to pass error messages to HTTP clients.
 */
@ToString(includeNames = true)
@EqualsAndHashCode
class HttpErrorMessage {

  /**
   * Exception stack trace
   */
  String stackTrace

  /**
   * Errors which do not correspond to a field
   */
  List<String> messages = []

  /**
   * Errors corresponding to bean fields (probably validation errors)
   */
  Map<String, List<String>> fieldMessages = [:]

  /**
   * URI which generated the error
   */
  String requestUri

  /**
   * Date at which the error occurred (defaults to constructed time)
   */
  Date date = new Date()

  /**
   * User who generated the error
   */
  String user

  /**
   * Http status code (default = 500)
   */
  Integer statusCode = 500

  /**
   * Determines if the error produces is a client or server related error
   * @return
   */
  public Boolean isClientError() {
    // TODO this is lazy.. clean it up
    return statusCode < 500
  }
}
