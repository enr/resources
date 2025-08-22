package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ResourceTest {

  @Test
  void exists_shouldReturnTrue_whenResourceExists() {
    TestResource resource = new TestResource(true);

    boolean result = resource.exists();

    assertThat(result).as("exists() should return true when resource exists").isTrue();
  }

  @Test
  void exists_shouldReturnFalse_whenResourceDoesNotExist() {
    TestResource resource = new TestResource(false);

    boolean result = resource.exists();

    assertThat(result).as("exists() should return false when resource does not exist").isFalse();
  }

  @Test
  void getAsBytes_shouldReturnCorrectBytes() {
    String content = "test content";
    TestResource resource = new TestResource(true, content);

    byte[] result = resource.getAsBytes();

    assertThat(result).as("getAsBytes() should return correct bytes").isEqualTo(content.getBytes());
  }

  @Test
  void getAsBytes_shouldThrowException_whenResourceDoesNotExist() {
    TestResource resource = new TestResource(false);

    assertThatThrownBy(() -> resource.getAsBytes())
        .as("getAsBytes() should throw exception when resource does not exist").isInstanceOf(RuntimeException.class)
        .hasMessage("Resource does not exist");
  }

  @Test
  void getAsInputStream_shouldReturnCorrectInputStream() {
    String content = "test content";
    TestResource resource = new TestResource(true, content);

    InputStream result = resource.getAsInputStream();

    assertThat(result).as("getAsInputStream() should return correct InputStream").isNotNull();
    assertThat(result).as("getAsInputStream() should return ByteArrayInputStream")
        .isInstanceOf(ByteArrayInputStream.class);
  }

  @Test
  void getAsInputStream_shouldThrowException_whenResourceDoesNotExist() {
    TestResource resource = new TestResource(false);

    assertThatThrownBy(() -> resource.getAsInputStream())
        .as("getAsInputStream() should throw exception when resource does not exist")
        .isInstanceOf(RuntimeException.class).hasMessage("Resource does not exist");
  }

  @Test
  void getAsString_shouldReturnCorrectString() {
    String content = "test content";
    TestResource resource = new TestResource(true, content);

    String result = resource.getAsString();

    assertThat(result).as("getAsString() should return correct string").isEqualTo(content);
  }

  @Test
  void getAsString_shouldThrowException_whenResourceDoesNotExist() {
    TestResource resource = new TestResource(false);

    assertThatThrownBy(() -> resource.getAsString())
        .as("getAsString() should throw exception when resource does not exist").isInstanceOf(RuntimeException.class)
        .hasMessage("Resource does not exist");
  }

  @ParameterizedTest
  @EnumSource(PathConversionStrategy.class)
  void getAsPath_shouldThrowException_whenResourceDoesNotExist(PathConversionStrategy strategy) {
    TestResource resource = new TestResource(false);

    assertThatThrownBy(() -> resource.getAsPath(strategy))
        .as("getAsPath() should throw exception when resource does not exist").isInstanceOf(RuntimeException.class)
        .hasMessage("Resource does not exist");
  }

  @Test
  void getAsPath_shouldReturnPath_whenResourceExists() {
    TestResource resource = new TestResource(true, "test content");
    resource.setPath(Path.of("/test/path"));

    Path result = resource.getAsPath(PathConversionStrategy.STRICT);

    assertThat(result).as("getAsPath() should return correct Path").isEqualTo(Path.of("/test/path"));
  }

  @Test
  void getAsPath_shouldThrowException_whenPathIsNotSet() {
    TestResource resource = new TestResource(true, "test content");

    assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.STRICT))
        .as("getAsPath() should throw exception when path is not set").isInstanceOf(RuntimeException.class)
        .hasMessage("Path not set");
  }

  // Test helper class
  private static class TestResource implements Resource {
    private final boolean exists;
    private final String content;
    private Path path;

    TestResource(boolean exists) {
      this(exists, "");
    }

    TestResource(boolean exists, String content) {
      this.exists = exists;
      this.content = content;
    }

    void setPath(Path path) {
      this.path = path;
    }

    @Override
    public boolean exists() {
      return exists;
    }

    @Override
    public byte[] getAsBytes() {
      if (!exists) {
        throw new RuntimeException("Resource does not exist");
      }
      return content.getBytes();
    }

    @Override
    public InputStream getAsInputStream() {
      if (!exists) {
        throw new RuntimeException("Resource does not exist");
      }
      return new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public String getAsString() {
      if (!exists) {
        throw new RuntimeException("Resource does not exist");
      }
      return content;
    }

    @Override
    public Path getAsPath(PathConversionStrategy strategy) {
      if (!exists) {
        throw new RuntimeException("Resource does not exist");
      }
      if (path == null) {
        throw new RuntimeException("Path not set");
      }
      return path;
    }
  }
}
