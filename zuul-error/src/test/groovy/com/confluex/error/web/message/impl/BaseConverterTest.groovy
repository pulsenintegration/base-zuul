package com.confluex.error.web.message.impl

import com.sun.net.httpserver.HttpPrincipal
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import com.confluex.error.web.message.HttpErrorMessage
import org.apache.commons.lang.exception.ExceptionUtils

abstract class BaseConverterTest {
  MockHttpServletRequest mockRequest

  abstract BaseConverter getConverter()

  @Before
  void createRequest() {
    mockRequest = new MockHttpServletRequest()
    mockRequest.requestURI = "/test"
    mockRequest.userPrincipal = new HttpPrincipal("testUser", "testRealm")
  }


  void assertCommonValuesAreSet(Throwable t, HttpErrorMessage message) {
    assert message.requestUri == "/test"
    assert message.user == "testUser"
    if (message.isClientError()) {
      assert !message.stackTrace
    }
    else {
      assert message.stackTrace == ExceptionUtils.getStackTrace(t)
    }
    assert message.date
    assert message.statusCode == converter.statusCode
  }
}
