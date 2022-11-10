package io.github.mufasa1976.openliberty.sample.config;

import io.github.mufasa1976.openliberty.sample.JndiConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@WebListener
@Slf4j
public class DatabaseInitializer implements ServletContextListener {
  @Resource(lookup = JndiConstants.DATASOURCE)
  private DataSource dataSource;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    Flyway.configure(this.getClass().getClassLoader())
          .dataSource(dataSource)
          .baselineOnMigrate(true)
          .baselineVersion("0")
          .load()
          .migrate();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}
}
