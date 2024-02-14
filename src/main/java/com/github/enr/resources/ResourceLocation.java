package com.github.enr.resources;

public interface ResourceLocation {

  Resource get(String location);

  /**
   * @param path The path to a resource including a prefix appended by a colon. Ex (classpath:, file:)
   * @return Whether the given resource loader supports the path
   */
  boolean supports(String path);

}
