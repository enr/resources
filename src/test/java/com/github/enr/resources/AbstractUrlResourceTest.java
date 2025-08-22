package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AbstractUrlResourceTest {

  @Test
  void constructor_shouldSetLocation() {
    TestUrlResource resource = new TestUrlResource("test-location");
    assertThat(resource.getLocation()).as("location should be set").isEqualTo("test-location");
  }

  @Test
  void exists_shouldReturnTrue_whenUrlIsPresent() throws MalformedURLException {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrl(Optional.of(new URL("http://example.com")));
    
    boolean result = resource.exists();
    
    assertThat(result).as("exists() should return true when URL is present").isTrue();
  }

  @Test
  void exists_shouldReturnFalse_whenUrlIsEmpty() {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrl(Optional.empty());
    
    boolean result = resource.exists();
    
    assertThat(result).as("exists() should return false when URL is empty").isFalse();
  }

  @Test
  void exists_shouldReturnFalse_whenUrlThrowsException() {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrlToThrowException(true);
    
    boolean result = resource.exists();
    
    assertThat(result).as("exists() should return false when URL throws exception").isFalse();
  }

  @Test
  void getAsBytes_shouldReturnBytesFromInputStream() {
    String content = "test content";
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStream(new ByteArrayInputStream(content.getBytes()));
    
    byte[] result = resource.getAsBytes();
    
    assertThat(result).as("getAsBytes() should return correct bytes").isEqualTo(content.getBytes());
  }

  @Test
  void getAsBytes_shouldReturnEmptyArray_whenInputStreamIsNull() {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStream(null);
    
    byte[] result = resource.getAsBytes();
    
    assertThat(result).as("getAsBytes() should return empty array when InputStream is null").isEmpty();
  }

  @Test
  void getAsBytes_shouldThrowException_whenInputStreamThrowsException() {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStreamToThrowException(true);
    
    assertThatThrownBy(() -> resource.getAsBytes())
        .as("getAsBytes() should throw ResourceLoadingException when InputStream throws exception")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessageContaining("error loading resource test-location");
  }

  @Test
  void getAsInputStream_shouldReturnInputStream() {
    InputStream expectedInputStream = new ByteArrayInputStream("test".getBytes());
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStream(expectedInputStream);
    
    InputStream result = resource.getAsInputStream();
    
    assertThat(result).as("getAsInputStream() should return correct InputStream").isEqualTo(expectedInputStream);
  }

  @Test
  void getAsInputStream_shouldThrowException_whenUrlIsEmpty() {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrl(Optional.empty());
    
    assertThatThrownBy(() -> resource.getAsInputStream())
        .as("getAsInputStream() should throw ResourceLoadingException when URL is empty")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessageContaining("resource not found test-location");
  }

  @Test
  void getAsString_shouldReturnStringFromBytes() {
    String content = "test content";
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStream(new ByteArrayInputStream(content.getBytes()));
    
    String result = resource.getAsString();
    
    assertThat(result).as("getAsString() should return correct string").isEqualTo(content);
  }

  @Test
  void getAsPath_shouldThrowException_whenLocationIsNull() throws MalformedURLException {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrl(Optional.of(new URL("http://example.com")));
    
    assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.STRICT))
        .as("getAsPath() should throw ResourceLoadingException when location is null")
        .isInstanceOf(ResourceLoadingException.class);
  }

  @ParameterizedTest
  @EnumSource(PathConversionStrategy.class)
  void getAsPath_shouldThrowException_whenUrlIsEmpty(PathConversionStrategy strategy) {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrl(Optional.empty());
    
    assertThatThrownBy(() -> resource.getAsPath(strategy))
        .as("getAsPath() should throw ResourceLoadingException when URL is empty")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessageContaining("resource not found test-location");
  }

  @Test
  void getAsPath_shouldThrowException_withStrictStrategy() throws MalformedURLException {
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setUrl(Optional.of(new URL("http://example.com")));
    
    assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.STRICT))
        .as("getAsPath() should throw ResourceLoadingException with STRICT strategy")
        .isInstanceOf(ResourceLoadingException.class)
        .hasMessageContaining("Cannot convert URL resource to Path with STRICT strategy");
  }

  @Test
  void getAsPath_shouldCreateTemporaryFile_withLenientStrategy(@TempDir Path tempDir) throws Exception {
    String content = "test content";
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStream(new ByteArrayInputStream(content.getBytes()));
    resource.setUrl(Optional.of(new URL("http://example.com")));
    
    Path result = resource.getAsPath(PathConversionStrategy.LENIENT);
    
    assertThat(result).as("getAsPath() should return a Path").isNotNull();
    assertThat(Files.exists(result)).as("temporary file should exist").isTrue();
    assertThat(Files.readString(result)).as("temporary file should contain correct content").isEqualTo(content);
    
    // Clean up
    Files.deleteIfExists(result);
  }

  @Test
  void getAsPath_shouldCreateTemporaryFile_withForceTemporaryStrategy(@TempDir Path tempDir) throws Exception {
    String content = "test content";
    TestUrlResource resource = new TestUrlResource("test-location");
    resource.setInputStream(new ByteArrayInputStream(content.getBytes()));
    resource.setUrl(Optional.of(new URL("http://example.com")));
    
    Path result = resource.getAsPath(PathConversionStrategy.FORCE_TEMPORARY);
    
    assertThat(result).as("getAsPath() should return a Path").isNotNull();
    assertThat(Files.exists(result)).as("temporary file should exist").isTrue();
    assertThat(Files.readString(result)).as("temporary file should contain correct content").isEqualTo(content);
    
    // Clean up
    Files.deleteIfExists(result);
  }

  // Test helper class
  private static class TestUrlResource extends AbstractUrlResource {
    private Optional<URL> url = Optional.empty();
    private InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    private boolean urlThrowsException = false;
    private boolean inputStreamThrowsException = false;

    TestUrlResource(String location) {
      super(location);
    }

    void setUrl(Optional<URL> url) {
      this.url = url;
    }

    void setInputStream(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    void setUrlToThrowException(boolean throwsException) {
      this.urlThrowsException = throwsException;
    }

    void setInputStreamToThrowException(boolean throwsException) {
      this.inputStreamThrowsException = throwsException;
    }

    String getLocation() {
      return "test-location";
    }

    @Override
    protected Optional<URL> url(String location) {
      if (urlThrowsException) {
        throw new RuntimeException("Test exception");
      }
      return url;
    }

    @Override
    public InputStream getAsInputStream() {
      if (inputStreamThrowsException) {
        throw new RuntimeException("Test exception");
      }
      return inputStream;
    }
  }
}
