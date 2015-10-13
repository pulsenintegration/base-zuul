package com.confluex.error.web.message.impl

import com.sun.net.httpserver.HttpPrincipal
import com.confluex.error.ValidationException
import com.confluex.error.Widget
import org.junit.Before
import org.junit.Test
import org.springframework.context.MessageSource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.ObjectError

import static org.mockito.Mockito.*

class ValidationConverterTest extends BaseConverterTest {

  ValidationConverter converter


  @Before
  void createConverter() {
    converter = new ValidationConverter(messageSource: mock(MessageSource))
  }

  @Test
  @SuppressWarnings("GroovyAssignabilityCheck")
  void shouldResolveMessagesByMessageSource() {
    def errors = [
            new ObjectError("widget", ["widget.error.a"] as String[], ["arg 1"] as String[], "error #1"),
            new ObjectError("widget", ["widget.error.b"] as String[], ["arg 2"] as String[], "error #2")
    ]
    when(converter.messageSource.getMessage(errors[0], mockRequest.locale)).thenReturn("error a")
    when(converter.messageSource.getMessage(errors[1], mockRequest.locale)).thenReturn("error b")
    def messages = converter.resolveMessages(errors, mockRequest)
    assert messages == ["error a", "error b"]
  }

  @Test
  void shouldResolveMessagesUsingDefaultMessageIfNoMessageSourceIsAvailable() {
    def errors = [new ObjectError("widget", "error #1"), new ObjectError("widget", "error #2")]
    converter.messageSource = null
    def messages = converter.resolveMessages(errors, mockRequest)
    assert messages == ["error #1", "error #2"]
  }


  @Test
  void shouldConvertValidationExceptions() {
    def widget = new Widget()
    def errors = new BeanPropertyBindingResult(widget, "widget")
    errors.reject(null, "Widget is really a gadget")
    errors.rejectValue("name", null, "Must have at least 2 characters")
    errors.rejectValue("name", null, "Must not contain special characters")
    errors.rejectValue("components", null, "Too many components (100 max)")

    def ex = new ValidationException(errors)
    def message = converter.convert(ex, mockRequest)
    assert message.messages == ["Widget is really a gadget"]
    assert message.fieldMessages == [
            name: ["Must have at least 2 characters", "Must not contain special characters"],
            components: ["Too many components (100 max)"]
    ]
    assertCommonValuesAreSet(ex, message)
  }
}
