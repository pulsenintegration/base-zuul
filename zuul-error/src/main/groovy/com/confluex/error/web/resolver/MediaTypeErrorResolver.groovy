package com.confluex.error.web.resolver

import org.springframework.http.MediaType
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Delegates to the ErrorResolver configured for a given media type.
 */
class MediaTypeErrorResolver implements HandlerExceptionResolver {
  /**
   * Configures media types to HandlerExceptionResolver implementations
   */
  Map<String, HandlerExceptionResolver> mappings = [:]

  /**
   * HandlerExceptionResolver to use if media type is not mapped. default = DefaultErrorResolver
   */
  HandlerExceptionResolver defaultResolver = new DefaultErrorResolver()

  @Override
  ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    def mediaType = request.contentType ? MediaType.parseMediaType(request.contentType) : null
    def resolver = mappings[mediaType?.subtype] ?: defaultResolver
    return resolver.resolveException(request, response, handler, ex)
  }
}
