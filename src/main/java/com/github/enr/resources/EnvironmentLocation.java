package com.github.enr.resources;

import com.github.enr.system.EnvironmentSource;

public class EnvironmentLocation implements ResourceLocation {

  private static final String PREFIX = "env:";

  private final EnvironmentSource env;

  public EnvironmentLocation() {
    this(new EnvironmentSource() {});
  }

  public EnvironmentLocation(EnvironmentSource env) {
    this.env = env;
  }

  @Override
  public Resource get(String location) {
    return new EnvironmentResource(Strings.removeStart(location, PREFIX), env);
  }

  @Override
  public boolean supports(String path) {
    if (path == null) {
      return false;
    }
    String p = path.trim();
    return p.startsWith(PREFIX) && p.length() > PREFIX.length();
  }

}
