package com.github.enr.resources;

import java.io.InputStream;
import java.nio.file.Path;

/*
 * interface for abstracting access to low-level resources identified from a pseudo uri
 */
public interface Resource {

  boolean exists();

  byte[] getAsBytes();

  InputStream getAsInputStream();

  String getAsString();

  /**
   * Converts this resource to a Path using the specified conversion strategy.
   *
   * @param strategy the conversion strategy to use
   * @return the Path representation of this resource
   * @throws ResourceLoadingException if the conversion is not possible with the given strategy
   */
  Path getAsPath(PathConversionStrategy strategy);

  /**
   * Converts this resource to a Path using the default conversion strategy (STRICT).
   *
   * @return the Path representation of this resource
   * @throws ResourceLoadingException if the conversion is not possible
   */
  default Path getAsPath() {
    return getAsPath(PathConversionStrategy.STRICT);
  }
}
