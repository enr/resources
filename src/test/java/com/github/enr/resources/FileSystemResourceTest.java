package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemResourceTest {

  @Test
  void exists_returnsFalse_forMissingFile() {
    FileSystemResource resource = new FileSystemResource("/tmp/does-not-exist-" + System.nanoTime() + ".txt");
    assertThat(resource.exists()).isFalse();
  }

  @Test
  void getAsBytes_throwsResourceLoadingException_forMissingFile() {
    FileSystemResource resource = new FileSystemResource("/tmp/does-not-exist-" + System.nanoTime() + ".txt");
    assertThatThrownBy(resource::getAsBytes).isInstanceOf(ResourceLoadingException.class);
  }

  @Test
  void getAsString_throwsResourceLoadingException_forMissingFile() {
    FileSystemResource resource = new FileSystemResource("/tmp/does-not-exist-" + System.nanoTime() + ".txt");
    assertThatThrownBy(resource::getAsString).isInstanceOf(ResourceLoadingException.class);
  }

  @Test
  void getAsString_returnsEmptyString_forEmptyFile(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("empty.txt");
    Files.createFile(file);
    FileSystemResource resource = new FileSystemResource(file.toString());
    assertThat(resource.exists()).isTrue();
    assertThat(resource.getAsString()).isEmpty();
  }

  @Test
  void getAsString_withCharset_decodesCorrectly(@TempDir Path dir) throws IOException {
    String content = "héllo";
    Path file = dir.resolve("latin1.txt");
    Files.write(file, content.getBytes(StandardCharsets.ISO_8859_1));

    FileSystemResource resource = new FileSystemResource(file.toString());
    assertThat(resource.getAsString(StandardCharsets.ISO_8859_1)).isEqualTo(content);
  }

  @Test
  void getAsPath_returnsAbsoluteNormalizedPath(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("data.txt");
    Files.writeString(file, "data");

    FileSystemResource resource = new FileSystemResource(file.toString());
    Path result = resource.getAsPath(PathConversionStrategy.STRICT);

    assertThat(result).isAbsolute();
    assertThat(result.toString()).doesNotContain("..");
    assertThat(Files.readString(result)).isEqualTo("data");
  }

  @Test
  void getAsInputStream_throwsResourceLoadingException_forDirectory(@TempDir Path dir) {
    FileSystemResource resource = new FileSystemResource(dir.toString());
    // A directory exists but its contents cannot be read as a byte stream
    assertThat(resource.exists()).isTrue();
    assertThatThrownBy(resource::getAsBytes).isInstanceOf(ResourceLoadingException.class);
  }
}
