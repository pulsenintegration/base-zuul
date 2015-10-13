package com.confluex.error.web.resolver

import groovy.json.JsonBuilder
import com.confluex.error.web.message.HttpErrorMessageConverter
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.MediaType

/**
 * Renders HttpErrorMessages as JSON to HTTP Clients
 */
class JsonErrorResolver implements HandlerExceptionResolver {

    HttpErrorMessageConverter httpErrorMessageConverter

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def error = httpErrorMessageConverter.convert(ex, request)
        response.status = error.statusCode
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        def writer = new OutputStreamWriter(response.outputStream)
        def json = new JsonBuilder()
        json {
            requestUri(error.requestUri)
            date(error.date.time)
            user(error.user)
            statusCode(error.statusCode)
            messages(error.messages)
            fieldMessages(error.fieldMessages)
            stackTrace(error.stackTrace)
        }
        json.writeTo(writer)
        writer.close()
        return new ModelAndView()
    }

}
