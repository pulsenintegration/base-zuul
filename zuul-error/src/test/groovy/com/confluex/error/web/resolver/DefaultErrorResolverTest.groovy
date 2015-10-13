package com.confluex.error.web.resolver

import com.confluex.error.web.message.HttpErrorMessage
import com.confluex.error.web.message.HttpErrorMessageConverter
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import static org.mockito.Mockito.*

class DefaultErrorResolverTest {

  DefaultErrorResolver resolver
  MockHttpServletRequest mockRequest
  MockHttpServletResponse mockResponse

  @Before
  void createResolver() {
    resolver = new DefaultErrorResolver(httpErrorMessageConverter: mock(HttpErrorMessageConverter))
    mockRequest = new MockHttpServletRequest()
    mockResponse = new MockHttpServletResponse()
  }

  @Test
  void shouldConvertExceptionAndUseViewFromMappingFromHttpErrorMessage() {
    def exception = new RuntimeException("test: not found")
    def message = new HttpErrorMessage(messages: ["test: not found"], statusCode: 404)
    when(resolver.httpErrorMessageConverter.convert(exception, mockRequest)).thenReturn(message)
    def mv = resolver.resolveException(mockRequest, mockResponse, null, exception)
    verify(resolver.httpErrorMessageConverter).convert(exception, mockRequest)
    assert mv.viewName == resolver.statusToViewMappings[404]
    assert mv.model[DefaultErrorResolver.MODEL_ATTRIBUTE_ERROR_MESSAGE] == message
    assert mockResponse.status == 404
  }


  @Test
  void shouldConvertExceptionAndUseDefaultViewIfNotConfigured() {
    def exception = new RuntimeException("test: unmapped code")
    def message = new HttpErrorMessage(messages: ["test: unmapped code"], statusCode: 1093)
    when(resolver.httpErrorMessageConverter.convert(exception, mockRequest)).thenReturn(message)
    def mv = resolver.resolveException(mockRequest, mockResponse, null, exception)
    verify(resolver.httpErrorMessageConverter).convert(exception, mockRequest)
    assert !resolver.statusToViewMappings.containsKey(1093)
    assert mv.viewName == resolver.defaultView
    assert mv.model[DefaultErrorResolver.MODEL_ATTRIBUTE_ERROR_MESSAGE] == message
    assert mockResponse.status == 1093
  }
}
