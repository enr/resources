package com.github.enr.resources;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

class FilePath {

  /**
   * Resolves the absolute path to the given file. Path will be normalized (eg with / as file
   * separator).
   *
   * @param file
   */
  public static String absoluteNormalized(File file) {
    File f = Objects.requireNonNull(file, "file cannot be null");
    return f.toPath().toAbsolutePath().normalize().toString();
  }

  public static String absoluteNormalized(String file) {
    String f = Objects.requireNonNull(file, "file cannot be null");
    return Paths.get(f).toAbsolutePath().normalize().toString();
  }

  /**
   * ToSlash returns the result of replacing each separator character in path with a slash ('/')
   * character. Multiple separators are replaced by multiple slashes.
   *
   * @param path
   * @return
   * @see https://golang.org/pkg/path/filepath
   */
  public static String toSlash(String path) {
    // if sep char == / : return path
    return Objects.requireNonNull(path, "Path cannot be null").replace(File.separatorChar, '/');
  }

}
