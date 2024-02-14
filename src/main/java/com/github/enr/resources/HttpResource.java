package com.github.enr.resources;

import java.io.InputStream;

public class HttpResource implements Resource {

  public HttpResource(String location) {
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public byte[] getAsBytes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getAsInputStream() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getAsString() {
    // TODO Auto-generated method stub
    return null;
  }

}
