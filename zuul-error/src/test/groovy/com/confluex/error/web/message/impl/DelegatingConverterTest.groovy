package com.confluex.error.web.message.impl

import com.confluex.error.ConflictingOperationException
import com.confluex.error.ValidationException
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest

import javax.validation.ConstraintViolationException

class DelegatingConverterTest {

  DelegatingConverter converter

  @Before
  void createConverter() {
    // not using mocks here because I want to test the generics and reflections
    // used to determine the correct converter
    converter = new DelegatingConverter(
            converters: [
                    new ConflictingOperationConverter(),
                    new CatchAllConverter()
            ]
    )
  }

  @Test
  void shouldFindCorrectAssignedTypeFromConverter() {
    assert converter.getAssignedType(new ValidationConverter()) == ValidationException
    assert converter.getAssignedType(new CatchAllConverter()) == Throwable
    assert converter.getAssignedType(new ConstraintViolationConverter()) == ConstraintViolationException
  }

  @Test
  void shouldFindMatchingExceptionFromNestedExceptions() {
    def exception = new RuntimeException("outter",
            new ConflictingOperationException("middle",
                    new IllegalArgumentException("inner")
            )
    )

    def error = converter.findSupportedError(exception, ConflictingOperationException)
    assert error.class == ConflictingOperationException
    assert error.message == "middle"

    error = converter.findSupportedError(exception, RuntimeException)
    assert error.class == RuntimeException
    assert error.message == "outter"

    error = converter.findSupportedError(exception, IllegalArgumentException)
    assert error.class == IllegalArgumentException
    assert error.message == "inner"

    error = converter.findSupportedError(exception, Throwable)
    assert error.class == RuntimeException
    assert error.message == "outter"

    assert !converter.findSupportedError(exception, ValidationException)
  }

  @Test
  void shouldConvertExceptionWithCorrectConverterAndException() {
    def iae = new IllegalArgumentException("test 1")
    def coe = new ConflictingOperationException("test 2")

    def message = converter.convert(iae, new MockHttpServletRequest())
    assert message.messages == ["Unhandled server error", iae.message]

    message = converter.convert(coe, new MockHttpServletRequest())
    assert message.messages == ["Conflicting Operation", coe.message]

    message = converter.convert(new RuntimeException(coe), new MockHttpServletRequest())
    assert message.messages == ["Conflicting Operation", coe.message]
  }

  @Test(expected=IllegalArgumentException)
  void shouldReThrowErrorWhenNoMatchingConverterIsFound() {
    converter.converters = [new ConflictingOperationConverter()]
    converter.convert(new IllegalArgumentException("test 1"), new MockHttpServletRequest())
  }
}
