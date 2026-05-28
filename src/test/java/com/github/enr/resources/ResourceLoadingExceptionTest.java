package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResourceLoadingExceptionTest {

  @Test
  void shouldBeInstanceOfRuntimeException() {
    assertThat(new ResourceLoadingException("test")).isInstanceOf(RuntimeException.class);
  }

  @Test
  void constructor_withMessageAndCause_shouldStoreBoth() {
    Throwable cause = new RuntimeException("root");
    ResourceLoadingException ex = new ResourceLoadingException("loading failed", cause);

    assertThat(ex.getMessage()).isEqualTo("loading failed");
    assertThat(ex.getCause()).isSameAs(cause);
  }

  @Test
  void constructor_withCause_shouldWrapCauseMessage() {
    Throwable cause = new RuntimeException("root cause");
    ResourceLoadingException ex = new ResourceLoadingException(cause);

    assertThat(ex.getCause()).isSameAs(cause);
    assertThat(ex.getMessage()).contains("RuntimeException");
  }
}
