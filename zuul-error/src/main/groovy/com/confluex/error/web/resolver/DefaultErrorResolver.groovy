package com.confluex.error.web.resolver

import groovy.util.logging.Slf4j
import com.confluex.error.web.message.HttpErrorMessageConverter
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.confluex.error.web.message.ErrorCodes

import static com.confluex.error.web.message.ErrorCodes.CLIENT_ERROR_NOT_AUTHORIZED
import static com.confluex.error.web.message.ErrorCodes.CLIENT_ERROR_CONFLICTING_OPERATION
import static com.confluex.error.web.message.ErrorCodes.CLIENT_ERROR_INVALID_ENTITY
import static com.confluex.error.web.message.ErrorCodes.CLIENT_ERROR_NOT_FOUND

/**
 * Renders HttpErrorMessages as HTML to HTTP clients
 */
@Slf4j
class DefaultErrorResolver implements HandlerExceptionResolver {

  static final String MODEL_ATTRIBUTE_ERROR_MESSAGE = "errorMessage"

  /**
   * Converts an exception to an appropriate HttpErrorMessage
   */
  HttpErrorMessageConverter httpErrorMessageConverter

  /**
   * <p>Mapping of HTTP status codes to views to render upon error. Http status code is
   * determined by the HttpErrorMessage created from the httpErrorMessageConverter</p>
   *
   * Defaults:
   * <ul>
   *    <li>SC_FORBIDDEN=/error/denied</li>
   *    <li>SC_FORBIDDEN=/error/conflict</li>
   *    <li>SC_NOT_ACCEPTABLE=/error/invalid</li>
   *    <li>SC_NOT_FOUND=/error/notFound</li>
   * </ul>
   */
  Map<Integer, String> statusToViewMappings = [
          (CLIENT_ERROR_NOT_AUTHORIZED): "/error/denied",
          (CLIENT_ERROR_CONFLICTING_OPERATION): "/error/conflict",
          (CLIENT_ERROR_INVALID_ENTITY): "/error/invalid",
          (CLIENT_ERROR_NOT_FOUND): "/error/notFound",
  ]

  /**
   * View to use if a HTTP status mapping is not found
   */
  String defaultView = "/error/default"

  /**
   * Converts the exception to a HttpErrorMessage and passes it to the configured view in the model (errorMessage)
   */
  ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    def errorMessage = httpErrorMessageConverter.convert(ex, request)
    response.status = errorMessage.statusCode
    def view = statusToViewMappings[errorMessage.statusCode] ?: defaultView
    return new ModelAndView(view, [(MODEL_ATTRIBUTE_ERROR_MESSAGE): errorMessage])
  }

}
