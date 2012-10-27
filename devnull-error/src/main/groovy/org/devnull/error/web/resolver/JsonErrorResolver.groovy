package org.devnull.error.web.resolver

import groovy.json.JsonBuilder

import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.devnull.error.web.message.HttpErrorMessageConverter

/**
 * Renders HttpErrorMessages as JSON to HTTP Clients
 */
class JsonErrorResolver implements HandlerExceptionResolver {

  HttpErrorMessageConverter httpErrorMessageConverter

  ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    def error = httpErrorMessageConverter.convert(ex, request)
    response.status = error.statusCode
    def writer = new OutputStreamWriter(response.outputStream)
    new JsonBuilder(error).writeTo(writer)
    writer.close()
    return new ModelAndView()
  }

}