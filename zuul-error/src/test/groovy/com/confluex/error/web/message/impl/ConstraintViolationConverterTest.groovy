package com.confluex.error.web.message.impl

import org.junit.Before
import org.junit.Test
import javax.validation.ConstraintViolationException
import javax.servlet.http.HttpServletResponse
import groovy.json.JsonSlurper
import javax.validation.ConstraintViolation

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ConstraintViolationConverterTest extends BaseConverterTest {
  ConstraintViolationConverter converter

  @Before
  void createConverter() {
    converter = new ConstraintViolationConverter()
  }

  @Test
  void shouldConvertConstraintViolationExceptions() {
    def violations = [
            mock(ConstraintViolation),
            mock(ConstraintViolation)
    ]
    //noinspection GroovyAssignabilityCheck
    def ex = new ConstraintViolationException("Testing validation errors", violations as Set)

    when(violations[0].getMessage()).thenReturn("error 1")
    when(violations[1].getMessage()).thenReturn("error 2")

    def message = converter.convert(ex, mockRequest)
    assert message.messages == ["error 1", "error 2"]
    assertCommonValuesAreSet(ex, message)
  }

}
