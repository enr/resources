package com.github.enr.resources;

public class ClasspathLocation implements ResourceLocation {

  private static final String PREFIX = "classpath:";

  @Override
  public Resource get(String location) {
    ClassLoader loader =
        firstNonNull(Thread.currentThread().getContextClassLoader(), ClasspathResource.class.getClassLoader());
    return new ClasspathResource(Strings.removeStart(location, PREFIX), loader);
  }

  @Override
  public boolean supports(String path) {
    if (path == null) {
      return false;
    }
    String p = path.trim();
    return p.startsWith(PREFIX) && p.length() > PREFIX.length();
  }

  private ClassLoader firstNonNull(ClassLoader first, ClassLoader second) {
    if (first != null) {
      return first;
    }
    if (second != null) {
      return second;
    }
    throw new ResourceLoadingException("no class loader");
  }
}
