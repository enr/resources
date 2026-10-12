package com.github.enr.resources;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Records the temp files with a given prefix existing at creation time, so that a test can detect
 * only the files it leaked itself and not leftovers of other runs (e.g. mutation testing) in the
 * shared temp directory.
 */
final class TempFileSnapshot {

  private final String prefix;
  private final Set<String> before;

  private TempFileSnapshot(String prefix) {
    this.prefix = prefix;
    this.before = new HashSet<>(list(prefix));
  }

  static TempFileSnapshot of(String prefix) {
    return new TempFileSnapshot(prefix);
  }

  List<String> newFiles() {
    return list(prefix).stream().filter(n -> !before.contains(n)).toList();
  }

  private static List<String> list(String prefix) {
    String[] names =
        new File(System.getProperty("java.io.tmpdir")).list((d, n) -> n.startsWith(prefix) && n.endsWith(".tmp"));
    return names == null ? List.of() : Arrays.asList(names);
  }
}
