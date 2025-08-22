package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceLocationTest {

  @Test
  void supports_shouldReturnTrue_whenLocationSupportsUri() {
    TestResourceLocation location = new TestResourceLocation(true);

    boolean result = location.supports("test-uri");

    assertThat(result).as("supports() should return true when location supports URI").isTrue();
  }

  @Test
  void supports_shouldReturnFalse_whenLocationDoesNotSupportUri() {
    TestResourceLocation location = new TestResourceLocation(false);

    boolean result = location.supports("test-uri");

    assertThat(result).as("supports() should return false when location does not support URI").isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"file:///path/to/file", "classpath:resource.txt", "http://example.com", "custom://uri"})
  void supports_shouldWorkWithDifferentUriTypes(String uri) {
    TestResourceLocation location = new TestResourceLocation(true);

    boolean result = location.supports(uri);

    assertThat(result).as("supports() should work with URI: " + uri).isTrue();
  }

  @Test
  void supports_shouldHandleNullUri() {
    TestResourceLocation location = new TestResourceLocation(true);

    boolean result = location.supports(null);

    assertThat(result).as("supports() should handle null URI").isTrue();
  }

  @Test
  void get_shouldReturnResource() {
    TestResource resource = new TestResource();
    TestResourceLocation location = new TestResourceLocation(true, resource);

    Resource result = location.get("test-uri");

    assertThat(result).as("get() should return correct resource").isEqualTo(resource);
  }

  @Test
  void get_shouldReturnNull_whenLocationDoesNotSupportUri() {
    TestResourceLocation location = new TestResourceLocation(false, null);

    Resource result = location.get("test-uri");

    assertThat(result).as("get() should return null when location does not support URI").isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"file:///path/to/file", "classpath:resource.txt", "http://example.com", "custom://uri"})
  void get_shouldWorkWithDifferentUriTypes(String uri) {
    TestResource resource = new TestResource();
    TestResourceLocation location = new TestResourceLocation(true, resource);

    Resource result = location.get(uri);

    assertThat(result).as("get() should work with URI: " + uri).isEqualTo(resource);
  }

  @Test
  void get_shouldHandleNullUri() {
    TestResource resource = new TestResource();
    TestResourceLocation location = new TestResourceLocation(true, resource);

    Resource result = location.get(null);

    assertThat(result).as("get() should handle null URI").isEqualTo(resource);
  }

  @Test
  void get_shouldThrowException_whenLocationThrowsException() {
    TestResourceLocation location = new TestResourceLocation(true, null, true);

    assertThatThrownBy(() -> location.get("test-uri")).as("get() should throw exception when location throws exception")
        .isInstanceOf(RuntimeException.class).hasMessage("Test exception");
  }

  // Test helper classes
  private static class TestResourceLocation implements ResourceLocation {
    private final boolean supports;
    private final Resource resource;
    private final boolean throwsException;

    TestResourceLocation(boolean supports) {
      this(supports, null, false);
    }

    TestResourceLocation(boolean supports, Resource resource) {
      this(supports, resource, false);
    }

    TestResourceLocation(boolean supports, Resource resource, boolean throwsException) {
      this.supports = supports;
      this.resource = resource;
      this.throwsException = throwsException;
    }

    @Override
    public boolean supports(String uri) {
      return supports;
    }

    @Override
    public Resource get(String uri) {
      if (throwsException) {
        throw new RuntimeException("Test exception");
      }
      return resource;
    }
  }

  private static class TestResource implements Resource {
    @Override
    public boolean exists() {
      return true;
    }

    @Override
    public byte[] getAsBytes() {
      return new byte[0];
    }

    @Override
    public java.io.InputStream getAsInputStream() {
      return new java.io.ByteArrayInputStream(new byte[0]);
    }

    @Override
    public String getAsString() {
      return "";
    }

    @Override
    public java.nio.file.Path getAsPath(PathConversionStrategy strategy) {
      throw new UnsupportedOperationException("Not implemented for test");
    }
  }
}
