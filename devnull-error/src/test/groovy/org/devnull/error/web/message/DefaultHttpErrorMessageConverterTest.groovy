package org.devnull.error.web.message

import com.sun.net.httpserver.HttpPrincipal
import org.apache.commons.lang.exception.ExceptionUtils
import org.devnull.error.ConflictingOperationException
import org.devnull.error.ValidationException
import org.devnull.error.Widget
import org.junit.Before
import org.junit.Test
import org.springframework.context.MessageSource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.ObjectError

import static org.mockito.Mockito.*

class DefaultHttpErrorMessageConverterTest {
  DefaultHttpErrorMessageConverter converter
  MockHttpServletRequest mockRequest

  @Before
  void createConverter() {
    converter = new DefaultHttpErrorMessageConverter(messageSource: mock(MessageSource))
    mockRequest = new MockHttpServletRequest()
    mockRequest.requestURI = "/test"
    mockRequest.userPrincipal = new HttpPrincipal("testUser", "testRealm")
  }

  @Test
  void shouldUseRootExceptionWhenUseRootCauseFlagIsTrue() {
    converter.useRootCause = true
    def ex = new RuntimeException("test container", new IllegalArgumentException("test cause"))
    assert converter.convert(ex, mockRequest).messages == ["test cause"]
  }


  @Test
  void shouldUseGivenExceptionWhenUseRootCauseIsTrueButThereIsNoRootCause() {
    converter.useRootCause = true
    def ex = new RuntimeException("test container")
    assert converter.convert(ex, mockRequest).messages == ["test container"]
  }

  @Test
  void shouldUseGivenExceptionWhenUseRootCauseFlagIsFalse() {
    converter.useRootCause = false
    def ex = new RuntimeException("test container", new IllegalArgumentException("test cause"))
    assert converter.convert(ex, mockRequest).messages == ["test container"]
  }

  @Test
  void shouldHaveNullUserIfNoUserPrincipalIsSet() {
    mockRequest.userPrincipal = null
    def ex = new RuntimeException("test container")
    assert !converter.convert(ex, mockRequest).user
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
    assert message.statusCode == 422
    assertCommonValuesAreSet(ex, message)
  }

  @Test
  void shouldConvertConflictingOperationExceptions() {
    def ex = new ConflictingOperationException("test conflict")
    def message = converter.convert(ex, mockRequest)
    assert message.messages == ["test conflict"]
    assert !message.fieldMessages
    assert message.statusCode == 409
    assertCommonValuesAreSet(ex, message)
  }

  void assertCommonValuesAreSet(Throwable t, HttpErrorMessage message) {
    assert message.requestUri == "/test"
    assert message.user == "testUser"
    assert message.stackTrace == ExceptionUtils.getStackTrace(t)
  }
}
