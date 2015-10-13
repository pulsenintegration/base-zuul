package com.confluex.error.web.message

import org.junit.Test

class HttpErrorMessageTest {
  @Test
  void shouldBeClientErrorIfLessThan500() {
    def message = new HttpErrorMessage(statusCode: 403)
    assert message.isClientError()
  }

  @Test
  void shouldNotBeClientErrorIfLGreaterThan499() {
    def message = new HttpErrorMessage(statusCode: 500)
    assert !message.isClientError()
  }
}
