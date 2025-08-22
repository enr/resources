package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceLoadingExceptionTest {

  @Test
  void constructor_withNoArgs_shouldCreateException() {
    ResourceLoadingException exception = new ResourceLoadingException();

    assertThat(exception).as("ResourceLoadingException with no args").isNotNull();
    assertThat(exception.getMessage()).as("message").isNull();
    assertThat(exception.getCause()).as("cause").isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"Test message", "Another message", "Error occurred", ""})
  void constructor_withMessage_shouldCreateExceptionWithMessage(String message) {
    ResourceLoadingException exception = new ResourceLoadingException(message);

    assertThat(exception).as("ResourceLoadingException with message: " + message).isNotNull();
    assertThat(exception.getMessage()).as("message").isEqualTo(message);
    assertThat(exception.getCause()).as("cause").isNull();
  }

  @Test
  void constructor_withNullMessage_shouldCreateExceptionWithNullMessage() {
    ResourceLoadingException exception = new ResourceLoadingException((String) null);

    assertThat(exception).as("ResourceLoadingException with null message").isNotNull();
    assertThat(exception.getMessage()).as("message").isNull();
    assertThat(exception.getCause()).as("cause").isNull();
  }

  @Test
  void constructor_withCause_shouldCreateExceptionWithCause() {
    Throwable cause = new RuntimeException("Original cause");
    ResourceLoadingException exception = new ResourceLoadingException(cause);

    assertThat(exception).as("ResourceLoadingException with cause").isNotNull();
    assertThat(exception.getMessage()).as("message").isEqualTo("java.lang.RuntimeException: Original cause");
    assertThat(exception.getCause()).as("cause").isEqualTo(cause);
  }

  @Test
  void constructor_withNullCause_shouldCreateExceptionWithNullCause() {
    ResourceLoadingException exception = new ResourceLoadingException((Throwable) null);

    assertThat(exception).as("ResourceLoadingException with null cause").isNotNull();
    assertThat(exception.getMessage()).as("message").isNull();
    assertThat(exception.getCause()).as("cause").isNull();
  }

  @Test
  void constructor_withMessageAndCause_shouldCreateExceptionWithBoth() {
    String message = "Resource loading failed";
    Throwable cause = new RuntimeException("Original cause");
    ResourceLoadingException exception = new ResourceLoadingException(message, cause);

    assertThat(exception).as("ResourceLoadingException with message and cause").isNotNull();
    assertThat(exception.getMessage()).as("message").isEqualTo(message);
    assertThat(exception.getCause()).as("cause").isEqualTo(cause);
  }

  @Test
  void constructor_withNullMessageAndCause_shouldCreateExceptionWithNullMessageAndCause() {
    Throwable cause = new RuntimeException("Original cause");
    ResourceLoadingException exception = new ResourceLoadingException((String) null, cause);

    assertThat(exception).as("ResourceLoadingException with null message and cause").isNotNull();
    assertThat(exception.getMessage()).as("message").isNull();
    assertThat(exception.getCause()).as("cause").isEqualTo(cause);
  }

  @Test
  void constructor_withMessageAndNullCause_shouldCreateExceptionWithMessageAndNullCause() {
    String message = "Resource loading failed";
    ResourceLoadingException exception = new ResourceLoadingException(message, null);

    assertThat(exception).as("ResourceLoadingException with message and null cause").isNotNull();
    assertThat(exception.getMessage()).as("message").isEqualTo(message);
    assertThat(exception.getCause()).as("cause").isNull();
  }

  @Test
  void constructor_withNullMessageAndNullCause_shouldCreateExceptionWithBothNull() {
    ResourceLoadingException exception = new ResourceLoadingException((String) null, null);

    assertThat(exception).as("ResourceLoadingException with null message and null cause").isNotNull();
    assertThat(exception.getMessage()).as("message").isNull();
    assertThat(exception.getCause()).as("cause").isNull();
  }

  @Test
  void exception_shouldBeInstanceOfRuntimeException() {
    ResourceLoadingException exception = new ResourceLoadingException("Test");

    assertThat(exception).as("ResourceLoadingException should be instance of RuntimeException")
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void exception_shouldPreserveStackTrace() {
    ResourceLoadingException exception = new ResourceLoadingException("Test");

    StackTraceElement[] stackTrace = exception.getStackTrace();
    assertThat(stackTrace).as("stack trace").isNotEmpty();
    assertThat(stackTrace[0].getClassName()).as("stack trace first element class name")
        .contains("ResourceLoadingExceptionTest");
  }
}
