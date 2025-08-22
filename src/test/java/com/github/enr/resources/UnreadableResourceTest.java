package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UnreadableResourceTest {

  @Test
  void exists_shouldAlwaysReturnFalse() {
    UnreadableResource resource = new UnreadableResource("test-location");
    
    boolean result = resource.exists();
    
    assertThat(result).as("exists()").isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"test-location", "file:///path/to/file", "classpath:resource.txt", "http://example.com"})
  void getAsBytes_shouldThrowResourceLoadingException(String location) {
    UnreadableResource resource = new UnreadableResource(location);
    
    assertThatThrownBy(() -> resource.getAsBytes())
        .as("getAsBytes() for location: " + location)
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource " + location);
  }

  @ParameterizedTest
  @ValueSource(strings = {"test-location", "file:///path/to/file", "classpath:resource.txt", "http://example.com"})
  void getAsInputStream_shouldThrowResourceLoadingException(String location) {
    UnreadableResource resource = new UnreadableResource(location);
    
    assertThatThrownBy(() -> resource.getAsInputStream())
        .as("getAsInputStream() for location: " + location)
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource " + location);
  }

  @ParameterizedTest
  @ValueSource(strings = {"test-location", "file:///path/to/file", "classpath:resource.txt", "http://example.com"})
  void getAsString_shouldThrowResourceLoadingException(String location) {
    UnreadableResource resource = new UnreadableResource(location);
    
    assertThatThrownBy(() -> resource.getAsString())
        .as("getAsString() for location: " + location)
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource " + location);
  }

  @ParameterizedTest
  @ValueSource(strings = {"test-location", "file:///path/to/file", "classpath:resource.txt", "http://example.com"})
  void getAsPath_shouldThrowResourceLoadingException(String location) {
    UnreadableResource resource = new UnreadableResource(location);
    
    assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.STRICT))
        .as("getAsPath() for location: " + location)
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource " + location);
  }

  @Test
  void getAsPath_shouldThrowResourceLoadingException_forAllStrategies() {
    UnreadableResource resource = new UnreadableResource("test-location");
    
    for (PathConversionStrategy strategy : PathConversionStrategy.values()) {
      assertThatThrownBy(() -> resource.getAsPath(strategy))
          .as("getAsPath() with strategy: " + strategy)
          .isInstanceOf(ResourceLoadingException.class)
          .hasMessage("Unreadable resource test-location");
    }
  }

  @Test
  void constructor_shouldAcceptNullLocation() {
    UnreadableResource resource = new UnreadableResource(null);
    
    assertThatThrownBy(() -> resource.getAsString())
        .as("getAsString() with null location")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource null");
  }

  @Test
  void constructor_shouldAcceptEmptyLocation() {
    UnreadableResource resource = new UnreadableResource("");
    
    assertThatThrownBy(() -> resource.getAsString())
        .as("getAsString() with empty location")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource ");
  }

  @Test
  void constructor_shouldAcceptSpecialCharactersInLocation() {
    String locationWithSpecialChars = "file:///path/with spaces and (parentheses) and [brackets]";
    UnreadableResource resource = new UnreadableResource(locationWithSpecialChars);
    
    assertThatThrownBy(() -> resource.getAsString())
        .as("getAsString() with special characters in location")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessage("Unreadable resource " + locationWithSpecialChars);
  }
}
