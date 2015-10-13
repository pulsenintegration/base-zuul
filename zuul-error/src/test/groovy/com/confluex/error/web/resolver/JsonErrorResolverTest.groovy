package com.confluex.error.web.resolver

import groovy.json.JsonSlurper
import com.confluex.error.web.message.HttpErrorMessage
import com.confluex.error.web.message.HttpErrorMessageConverter
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import static org.mockito.Mockito.*

class JsonErrorResolverTest {

    JsonErrorResolver resolver
    MockHttpServletRequest mockRequest
    MockHttpServletResponse mockResponse

    @Before
    void createResolver() {
        resolver = new JsonErrorResolver(httpErrorMessageConverter: mock(HttpErrorMessageConverter))
        mockRequest = new MockHttpServletRequest()
        mockResponse = new MockHttpServletResponse()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Test
    void shouldRenderConverterResponseAsJson() {
        def exception = new RuntimeException("test")
        def message = new HttpErrorMessage(
                messages: ["global message 1", "global message 2"],
                fieldMessages: [
                        fieldA: ["fieldA message 1", "fieldA message 2"],
                        fieldB: ["fieldB message 1"],
                ],
                stackTrace: "I'm a stacktrace",
                user: "username",
                statusCode: 501,
                requestUri: "/test"
        )
        when(resolver.httpErrorMessageConverter.convert(exception, mockRequest)).thenReturn(message)
        def mv = resolver.resolveException(mockRequest, mockResponse, null, exception)
        assert mv.isEmpty()
        assert mockResponse.status == message.statusCode
        assert mockResponse.contentType == "application/json"
        def json = new JsonSlurper().parseText(mockResponse.contentAsString)
        assert json.fieldMessages["fieldA"] == ["fieldA message 1", "fieldA message 2"]
        assert json.fieldMessages["fieldB"] == ["fieldB message 1"]
        assert json.messages == ["global message 1", "global message 2"]
        assert json.stackTrace == "I'm a stacktrace"
        assert json.requestUri == "/test"
        assert json.user == "username"
        assert new Date(json.date.toLong()).clearTime() == new Date().clearTime()
    }
}
