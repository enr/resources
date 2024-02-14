package com.github.enr.resources;

public class EnvironmentLocation implements ResourceLocation {

  private static final String PREFIX = "env:";

  @Override
  public Resource get(String location) {
    return new EnvironmentResource(Strings.removeStart(location, PREFIX));
  }

  @Override
  public boolean supports(String path) {
    return path != null && path.startsWith(PREFIX);
  }

}
