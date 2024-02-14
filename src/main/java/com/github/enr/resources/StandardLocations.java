package com.github.enr.resources;

public class StandardLocations {

  private StandardLocations() {
    // TODO Auto-generated constructor stub
  }

  public static final ResourceLocation CLASSPATH = new ClasspathLocation();

  public static PrioritizableResourceLocation[] withConventionalPriority() {
    return new PrioritizableResourceLocation[] {new PrioritizableResourceLocation(90, new ClasspathLocation()),
        new PrioritizableResourceLocation(80, new EnvironmentLocation()),
        new PrioritizableResourceLocation(10, new UrlLocation()),
        new PrioritizableResourceLocation(-100, new FileSystemLocation())};
  }
}
