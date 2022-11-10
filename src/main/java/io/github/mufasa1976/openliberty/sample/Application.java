package io.github.mufasa1976.openliberty.sample;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath(Application.BASE_PATH)
public class Application extends jakarta.ws.rs.core.Application {
  public static final String BASE_PATH = "api";
}
