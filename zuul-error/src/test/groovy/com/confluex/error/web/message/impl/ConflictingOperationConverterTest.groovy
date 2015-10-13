package com.confluex.error.web.message.impl

import com.confluex.error.ConflictingOperationException
import org.junit.Before
import org.junit.Test

class ConflictingOperationConverterTest extends BaseConverterTest {

  ConflictingOperationConverter converter

  @Before
  void createConverter() {
    converter = new ConflictingOperationConverter()
  }

  @Test
  void shouldConvertConflictingOperationExceptions() {
    def ex = new ConflictingOperationException("test conflict")
    def message = converter.convert(ex, mockRequest)
    assert message.messages == ["Conflicting Operation", "test conflict"]
    assert !message.fieldMessages
    assertCommonValuesAreSet(ex, message)
  }
}
