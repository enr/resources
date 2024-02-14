package com.github.enr.resources;

public interface ResourceLoader {

  Resource get(String location);

  /**
   * @param uri The path to a resource including a prefix appended by a colon. Ex (classpath:, file:)
   * @return Whether the given resource loader supports the path
   */
  boolean supports(String uri);

  public static ResourceLoader defaultInstance() {
    return forLocations(StandardLocations.withConventionalPriority());
  }

  public static ResourceLoader forLocations(PrioritizableResourceLocation... locations) {
    if (locations == null || locations.length < 1) {
      throw new IllegalArgumentException("Locations must not be empty");
    }
    return new CompositeResourceLoader(locations);
  }
}
