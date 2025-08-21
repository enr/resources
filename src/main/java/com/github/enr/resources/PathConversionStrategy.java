package com.github.enr.resources;

public enum PathConversionStrategy {
  /**
   * Convert only if the URL represents a direct file system path without the need for copies or
   * workarounds
   */
  STRICT,

  /**
   * Use workarounds like copying to a temporary file if necessary
   */
  LENIENT,

  /**
   * Force the creation of a temporary file
   */
  FORCE_TEMPORARY

}
