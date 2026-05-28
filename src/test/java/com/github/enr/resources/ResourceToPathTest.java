package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.enr.system.EnvironmentSource;

class ResourceToPathTest {

  @Test
  void fileSystemResource_getAsPath_returnsOriginalPath(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("sample.txt");
    Files.writeString(file, "test content");

    FileSystemResource resource = new FileSystemResource(file.toString());

    assertThat(resource.getAsPath()).isEqualTo(file.toAbsolutePath().normalize());
    assertThat(resource.getAsPath(PathConversionStrategy.STRICT)).isEqualTo(file.toAbsolutePath().normalize());
    assertThat(resource.getAsPath(PathConversionStrategy.LENIENT)).isEqualTo(file.toAbsolutePath().normalize());
    assertThat(resource.getAsPath(PathConversionStrategy.FORCE_TEMPORARY)).isEqualTo(file.toAbsolutePath().normalize());
  }

  @Test
  void environmentResource_strict_throwsResourceLoadingException() {
    EnvironmentResource resource = new EnvironmentResource("TEST_KEY", mapEnv("TEST_KEY", "value"));
    assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.STRICT))
        .isInstanceOf(ResourceLoadingException.class).hasMessageContaining("STRICT");
  }

  @Test
  void environmentResource_lenient_createsTempFileWithContents() throws IOException {
    EnvironmentResource resource = new EnvironmentResource("MSG", mapEnv("MSG", "env-value"));
    Path result = resource.getAsPath(PathConversionStrategy.LENIENT);
    try {
      assertThat(Files.exists(result)).isTrue();
      assertThat(Files.readString(result)).isEqualTo("env-value");
    } finally {
      Files.deleteIfExists(result);
    }
  }

  @Test
  void environmentResource_forceTemporary_createsTempFileWithContents() throws IOException {
    EnvironmentResource resource = new EnvironmentResource("MSG", mapEnv("MSG", "env-value"));
    Path result = resource.getAsPath(PathConversionStrategy.FORCE_TEMPORARY);
    try {
      assertThat(Files.exists(result)).isTrue();
      assertThat(Files.readString(result)).isEqualTo("env-value");
    } finally {
      Files.deleteIfExists(result);
    }
  }

  @Test
  void unreadableResource_getAsPath_throwsForAllStrategies() {
    UnreadableResource resource = new UnreadableResource("unreadable://test");
    for (PathConversionStrategy strategy : PathConversionStrategy.values()) {
      assertThatThrownBy(() -> resource.getAsPath(strategy)).isInstanceOf(ResourceLoadingException.class);
    }
    assertThatThrownBy(resource::getAsPath).isInstanceOf(ResourceLoadingException.class);
  }

  private static EnvironmentSource mapEnv(String key, String value) {
    return new EnvironmentSource() {
      private final Map<String, String> map = Map.of(key, value);

      @Override
      public Map<String, String> getEnv() {
        return map;
      }

      @Override
      public String getEnvironmentVar(String k) {
        return map.get(k);
      }
    };
  }
}
