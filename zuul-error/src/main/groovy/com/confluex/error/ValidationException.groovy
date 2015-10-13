package com.confluex.error

import groovy.transform.ToString
import org.springframework.validation.Errors

@ToString(includeNames = true)
class ValidationException extends DevNullException {
  Errors errors

  ValidationException(Errors errors, Exception e = null) {
    super("Invalid data", e)
    this.errors = errors
  }
}
