package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FileSystemResourceLocationTest {

  static Stream<ResourceSupportsTestCase> osAgnosticSupportsInput() {
    return Stream.of(
        // empty
        new ResourceSupportsTestCase(null, false), new ResourceSupportsTestCase("", false),
        new ResourceSupportsTestCase(" ", false), new ResourceSupportsTestCase("file:", false),
        // win
        new ResourceSupportsTestCase("file:C:/with/scheme", true),
        new ResourceSupportsTestCase("C:/win/with/slash", true),
        // new ResourceSupportsTestCase("C:\\with\\backslash", true),
        // happy paths
        new ResourceSupportsTestCase("file:/with/scheme", true),
        new ResourceSupportsTestCase("FILE:/upper/case/scheme", true),
        new ResourceSupportsTestCase("file:/path/with spaces", true),
        new ResourceSupportsTestCase("  file:/with/scheme", true), new ResourceSupportsTestCase("relative/path", true),
        new ResourceSupportsTestCase("./relative/path", true),
        new ResourceSupportsTestCase("file://./relative/path", true));
  }

  static Stream<ResourceSupportsTestCase> windowsSupportsInput() {
    return Stream.of(new ResourceSupportsTestCase("C:\\with\\backslash", true));
  }

  @ParameterizedTest
  @MethodSource("osAgnosticSupportsInput")
  void testSupports(ResourceSupportsTestCase t) {
    FileSystemLocation sut = new FileSystemLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }

  @EnabledOnOs({OS.WINDOWS})
  @ParameterizedTest
  @MethodSource("windowsSupportsInput")
  void testSupportsOnWin(ResourceSupportsTestCase t) {
    FileSystemLocation sut = new FileSystemLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }

  @Test
  void get_shouldDecodePercentEncodedFileUri(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("with space.txt");
    Files.writeString(file, "spaced");

    Resource resource = new FileSystemLocation().get(file.toUri().toString());

    assertThat(file.toUri().toString()).contains("%20");
    assertThat(resource.exists()).isTrue();
    assertThat(resource.getAsString()).isEqualTo("spaced");
  }

  @Test
  void get_shouldAcceptUnencodedSpacesAndUppercaseScheme(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("with space.txt");
    Files.writeString(file, "spaced");
    String path = FilePath.toSlash(file.toString());

    assertThat(new FileSystemLocation().get("file:" + path).getAsString()).isEqualTo("spaced");
    assertThat(new FileSystemLocation().get("FILE:" + path).getAsString()).isEqualTo("spaced");
  }

  @Test
  void defaultLoader_shouldRouteUppercaseFileScheme(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("upper.txt");
    Files.writeString(file, "upper");

    Resource resource = ResourceLoader.defaultInstance().get("FILE:" + FilePath.toSlash(file.toString()));

    assertThat(resource).isInstanceOf(FileSystemResource.class);
    assertThat(resource.getAsString()).isEqualTo("upper");
  }

  @EnabledOnOs({OS.WINDOWS})
  @Test
  void get_shouldHandleWindowsFileUri(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("win.txt");
    Files.writeString(file, "win");

    // file:///C:/... form
    assertThat(new FileSystemLocation().get(file.toUri().toString()).getAsString()).isEqualTo("win");
  }
}
