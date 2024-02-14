package com.github.enr.resources;

import java.net.URL;
import java.util.Optional;

public class ClasspathResource extends AbstractUrlResource {

  private final ClassLoader loader;

  public ClasspathResource(String location, ClassLoader loader) {
    super(location);
    this.loader = loader;
  }

  /*
   * Using ClassLoader the resource name specified as an input is always considered to be absolute.
   */
  @Override
  protected Optional<URL> url(String location) {
    String path = Strings.removeStart(location, "/");
    return Optional.ofNullable(loader.getResource(path));
  }

}
