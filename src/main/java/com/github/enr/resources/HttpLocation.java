package com.github.enr.resources;

public class HttpLocation implements ResourceLocation {

  @Override
  public Resource get(String location) {
    return new HttpResource(location);
  }

  @Override
  public boolean supports(String path) {
    return true;
  }

}
