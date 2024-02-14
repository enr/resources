package com.github.enr.resources;

import java.io.InputStream;

/*
 * interface for abstracting access to low-level resources identified from a pseudo uri
 */
public interface Resource {

  boolean exists();

  byte[] getAsBytes();

  InputStream getAsInputStream();

  String getAsString();
}
