package com.confluex.error

abstract class DevNullException extends RuntimeException {
  DevNullException(String message, Throwable ex = null) {
    super(message, ex)
  }
}
