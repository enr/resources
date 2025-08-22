package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CompositeResourceLoaderTest {

  @Test
  void get_shouldReturnResourceFromFirstSupportingLocation() {
    // Given
    TestResourceLocation location1 = new TestResourceLocation(false, null);
    TestResourceLocation location2 = new TestResourceLocation(true, new TestResource());

    PrioritizableResourceLocation prl1 = new PrioritizableResourceLocation(1, location1);
    PrioritizableResourceLocation prl2 = new PrioritizableResourceLocation(2, location2);

    CompositeResourceLoader loader = new CompositeResourceLoader(prl1, prl2);

    // When
    Resource result = loader.get("test-uri");

    // Then
    assertThat(result).as("get() should return resource from supporting location").isEqualTo(location2.get("test-uri"));
  }

  @Test
  void get_shouldReturnUnreadableResource_whenNoLocationSupportsUri() {
    // Given
    TestResourceLocation location1 = new TestResourceLocation(false, null);
    TestResourceLocation location2 = new TestResourceLocation(false, null);

    PrioritizableResourceLocation prl1 = new PrioritizableResourceLocation(1, location1);
    PrioritizableResourceLocation prl2 = new PrioritizableResourceLocation(2, location2);

    CompositeResourceLoader loader = new CompositeResourceLoader(prl1, prl2);

    // When
    Resource result = loader.get("unsupported-uri");

    // Then
    assertThat(result).as("get() should return UnreadableResource").isInstanceOf(UnreadableResource.class);
    assertThat(result.exists()).as("UnreadableResource should not exist").isFalse();
  }

  @Test
  void get_shouldThrowException_whenUriIsNull() {
    // Given
    TestResourceLocation location = new TestResourceLocation(true, new TestResource());
    PrioritizableResourceLocation prl = new PrioritizableResourceLocation(1, location);
    CompositeResourceLoader loader = new CompositeResourceLoader(prl);

    // When/Then
    assertThatThrownBy(() -> loader.get(null)).as("get() with null URI").isInstanceOf(NullPointerException.class);
  }

  @Test
  void supports_shouldReturnTrue_whenAnyLocationSupportsUri() {
    // Given
    TestResourceLocation location1 = new TestResourceLocation(false, null);
    TestResourceLocation location2 = new TestResourceLocation(true, new TestResource());

    PrioritizableResourceLocation prl1 = new PrioritizableResourceLocation(1, location1);
    PrioritizableResourceLocation prl2 = new PrioritizableResourceLocation(2, location2);

    CompositeResourceLoader loader = new CompositeResourceLoader(prl1, prl2);

    // When
    boolean result = loader.supports("supported-uri");

    // Then
    assertThat(result).as("supports() should return true when any location supports URI").isTrue();
  }

  @Test
  void supports_shouldReturnFalse_whenNoLocationSupportsUri() {
    // Given
    TestResourceLocation location1 = new TestResourceLocation(false, null);
    TestResourceLocation location2 = new TestResourceLocation(false, null);

    PrioritizableResourceLocation prl1 = new PrioritizableResourceLocation(1, location1);
    PrioritizableResourceLocation prl2 = new PrioritizableResourceLocation(2, location2);

    CompositeResourceLoader loader = new CompositeResourceLoader(prl1, prl2);

    // When
    boolean result = loader.supports("unsupported-uri");

    // Then
    assertThat(result).as("supports() should return false when no location supports URI").isFalse();
  }

  @Test
  void supports_shouldReturnFalse_whenUriIsNull() {
    // Given
    TestResourceLocation location = new TestResourceLocation(true, new TestResource());
    PrioritizableResourceLocation prl = new PrioritizableResourceLocation(1, location);
    CompositeResourceLoader loader = new CompositeResourceLoader(prl);

    // When
    boolean result = loader.supports(null);

    // Then
    assertThat(result).as("supports() should return false for null URI").isFalse();
  }

  @Test
  void constructor_shouldSortLocationsByPriority() {
    // Given
    TestResourceLocation location1 = new TestResourceLocation(true, new TestResource("resource1"));
    TestResourceLocation location2 = new TestResourceLocation(true, new TestResource("resource2"));
    TestResourceLocation location3 = new TestResourceLocation(true, new TestResource("resource3"));

    PrioritizableResourceLocation prl1 = new PrioritizableResourceLocation(1, location1); // lowest priority
    PrioritizableResourceLocation prl2 = new PrioritizableResourceLocation(5, location2); // highest priority
    PrioritizableResourceLocation prl3 = new PrioritizableResourceLocation(3, location3); // medium priority

    CompositeResourceLoader loader = new CompositeResourceLoader(prl1, prl2, prl3);

    // When
    Resource result = loader.get("test-uri");

    // Then
    assertThat(result).as("get() should return resource from highest priority location")
        .isEqualTo(location2.get("test-uri"));
  }

  @Test
  void constructor_shouldHandleEmptyLocationsArray() {
    // Given
    CompositeResourceLoader loader = new CompositeResourceLoader();

    // When
    boolean supports = loader.supports("any-uri");
    Resource resource = loader.get("any-uri");

    // Then
    assertThat(supports).as("supports() with empty locations").isFalse();
    assertThat(resource).as("get() with empty locations").isInstanceOf(UnreadableResource.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"file:///path/to/file", "classpath:resource.txt", "http://example.com", "custom://uri"})
  void get_shouldWorkWithDifferentUriTypes(String uri) {
    // Given
    TestResourceLocation location = new TestResourceLocation(true, new TestResource());
    PrioritizableResourceLocation prl = new PrioritizableResourceLocation(1, location);
    CompositeResourceLoader loader = new CompositeResourceLoader(prl);

    // When
    Resource result = loader.get(uri);

    // Then
    assertThat(result).as("get() with URI: " + uri).isEqualTo(location.get(uri));
  }

  // Test helper classes
  private static class TestResourceLocation implements ResourceLocation {
    private final boolean supports;
    private final Resource resource;

    TestResourceLocation(boolean supports, Resource resource) {
      this.supports = supports;
      this.resource = resource;
    }

    @Override
    public boolean supports(String uri) {
      return supports;
    }

    @Override
    public Resource get(String uri) {
      return resource;
    }
  }

  private static class TestResource implements Resource {
    private final String content;

    TestResource() {
      this("test content");
    }

    TestResource(String content) {
      this.content = content;
    }

    @Override
    public boolean exists() {
      return true;
    }

    @Override
    public byte[] getAsBytes() {
      return content.getBytes();
    }

    @Override
    public java.io.InputStream getAsInputStream() {
      return new java.io.ByteArrayInputStream(content.getBytes());
    }

    @Override
    public String getAsString() {
      return content;
    }

    @Override
    public String getAsString(java.nio.charset.Charset charset) {
      return content;
    }

    @Override
    public java.nio.file.Path getAsPath(PathConversionStrategy strategy) {
      throw new UnsupportedOperationException("Not implemented for test");
    }
  }
}
