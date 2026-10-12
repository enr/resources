package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

final class TempFiles {

  private TempFiles() {}

  /**
   * Copies the contents of the given source into a new temporary file. The source is opened before
   * the file is created, and the file is removed if the copy fails.
   */
  static Path copyToTempFile(String prefix, Supplier<InputStream> source) {
    try (InputStream is = source.get()) {
      Path tempFile = Files.createTempFile(prefix, ".tmp");
      try {
        Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
      } catch (IOException | RuntimeException e) {
        try {
          Files.deleteIfExists(tempFile);
        } catch (IOException suppressed) {
          e.addSuppressed(suppressed);
        }
        throw e;
      }
    } catch (IOException e) {
      throw new ResourceLoadingException("Error creating temporary file", e);
    }
  }
}
