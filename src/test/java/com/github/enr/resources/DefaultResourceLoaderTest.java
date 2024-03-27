package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultResourceLoaderTest {

  @Test
  void testFileSystemExists() throws IOException {
    String content = "test-content";
    Path tmp = Files.createTempFile("test-", ".tmp");
    Files.writeString(tmp, content);

    ResourceLoader resourceLoader = ResourceLoader.defaultInstance();
    Resource resource = resourceLoader.get(tmp.toString());
    assertThat(resource).as("resource").isInstanceOf(FileSystemResource.class);
    assertThat(resource.exists()).as("resource exists").isTrue();
    assertThat(resource.getAsString()).as("resource contents").isEqualTo(content);
  }

  @Test
  void testFileSystemNotExists() throws IOException {
    ResourceLoader resourceLoader = ResourceLoader.defaultInstance();
    Resource resource = resourceLoader.get("/tmp/test-resource-%s.tmp".formatted(System.currentTimeMillis()));
    assertThat(resource).as("resource").isInstanceOf(FileSystemResource.class);
    assertThat(resource.exists()).as("resource exists").isFalse();
  }

  @Test
  void testClasspath() throws IOException {
    String content = "test cp-01 txt";
    ResourceLoader resourceLoader = ResourceLoader.defaultInstance();
    Resource resource = resourceLoader.get("classpath:/com/github/enr/resources/cp-01.txt");
    assertThat(resource).as("resource").isInstanceOf(ClasspathResource.class);
    assertThat(resource.exists()).as("resource exists").isTrue();
    assertThat(resource.getAsString()).as("resource contents").isEqualTo(content);
  }

  @Test
  void testNotRespondingUrl() throws IOException {
    ResourceLoader resourceLoader = ResourceLoader.defaultInstance();
    Resource resource = resourceLoader.get("http://localhost:8099");
    assertThat(resource).as("resource").isInstanceOf(UrlResource.class);
    // an http url is considered "exists"
    assertThat(resource.exists()).as("resource exists").isTrue();
    Assertions.assertThrows(ResourceLoadingException.class, () -> resource.getAsString());
  }

}
