package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class FilePathTest {

  @Test
  void absoluteNormalized_withFile_shouldReturnNormalizedAbsolutePath(@TempDir Path tempDir) throws Exception {
    File testFile = Files.createFile(tempDir.resolve("test.txt")).toFile();
    
    String result = FilePath.absoluteNormalized(testFile);
    
    assertThat(result).as("absoluteNormalized with File").isEqualTo(testFile.getAbsolutePath());
  }

  @Test
  void absoluteNormalized_withString_shouldReturnNormalizedAbsolutePath(@TempDir Path tempDir) throws Exception {
    Path testPath = Files.createFile(tempDir.resolve("test.txt"));
    String pathString = testPath.toString();
    
    String result = FilePath.absoluteNormalized(pathString);
    
    assertThat(result).as("absoluteNormalized with String").isEqualTo(testPath.toAbsolutePath().normalize().toString());
  }

  @Test
  void absoluteNormalized_withFile_shouldThrowException_whenFileIsNull() {
    assertThatThrownBy(() -> FilePath.absoluteNormalized((File) null))
        .as("absoluteNormalized with null File")
        .isInstanceOf(NullPointerException.class)
        .hasMessage("file cannot be null");
  }

  @Test
  void absoluteNormalized_withString_shouldThrowException_whenStringIsNull() {
    assertThatThrownBy(() -> FilePath.absoluteNormalized((String) null))
        .as("absoluteNormalized with null String")
        .isInstanceOf(NullPointerException.class)
        .hasMessage("file cannot be null");
  }

  @ParameterizedTest
  @CsvSource({
      "path/to/file, path/to/file",
      "path\\to\\file, path/to/file",
      "C:\\Windows\\System32, C:/Windows/System32",
      "/usr/local/bin, /usr/local/bin",
      "relative\\path, relative/path"
  })
  void toSlash_shouldReplaceSeparatorsWithForwardSlash(String input, String expected) {
    String result = FilePath.toSlash(input);
    assertThat(result).as("toSlash('%s')").isEqualTo(expected);
  }

  @Test
  void toSlash_shouldReturnSameString_whenAlreadyUsingForwardSlashes() {
    String input = "path/to/file";
    String result = FilePath.toSlash(input);
    assertThat(result).as("toSlash with forward slashes").isEqualTo(input);
  }

  @Test
  void toSlash_shouldHandleMixedSeparators() {
    String input = "path\\to/file\\with\\mixed/separators";
    String result = FilePath.toSlash(input);
    assertThat(result).as("toSlash with mixed separators").isEqualTo("path/to/file/with/mixed/separators");
  }

  @Test
  void toSlash_shouldThrowException_whenPathIsNull() {
    assertThatThrownBy(() -> FilePath.toSlash(null))
        .as("toSlash with null path")
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Path cannot be null");
  }

  @Test
  void toSlash_shouldHandleEmptyString() {
    String result = FilePath.toSlash("");
    assertThat(result).as("toSlash with empty string").isEmpty();
  }

  @Test
  void toSlash_shouldHandleSingleCharacter() {
    String result = FilePath.toSlash("a");
    assertThat(result).as("toSlash with single character").isEqualTo("a");
  }

  @Test
  void toSlash_shouldHandleMultipleConsecutiveSeparators() {
    String input = "path\\\\to\\file";
    String result = FilePath.toSlash(input);
    assertThat(result).as("toSlash with multiple consecutive separators").isEqualTo("path//to/file");
  }
}
