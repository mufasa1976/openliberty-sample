package io.github.mufasa1976.openliberty.sample.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Path("sample")
@Slf4j
public class SampleController {
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHello(@QueryParam("name") String name) {
    return "Hello (we are winning), " + name;
  }
}
