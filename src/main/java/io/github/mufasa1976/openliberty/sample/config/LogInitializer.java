package io.github.mufasa1976.openliberty.sample.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.github.mufasa1976.openliberty.sample.JndiConstants;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class LogInitializer implements ServletContainerInitializer {
  private static final String JNDI_NAME_CONFIG_DIRECTORY = JndiConstants.CONFIGURATION_DIRECTORY;
  private static final String JNDI_NAME_LOG_CONFIG_FILENAME = JndiConstants.LOG_CONFIGURATION_FILE;

  private static Logger log = LoggerFactory.getLogger(LogInitializer.class);

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    getLogConfigurationFile().filter(LogInitializer::existsLogConfiguration)
                             .map(Paths::get)
                             .ifPresent(LogInitializer::initializeLogging);
  }

  private static Optional<String> getLogConfigurationFile() {
    try {
      final InitialContext initialContext = new InitialContext();
      return Optional.of(initialContext.lookup(JNDI_NAME_CONFIG_DIRECTORY) + "/" + initialContext.lookup(JNDI_NAME_LOG_CONFIG_FILENAME))
                     .filter(configurationFile -> !"/".equals(configurationFile));
    } catch (NamingException e) {
      log.error("Error while getting the LogConfiguration", e);
      return Optional.empty();
    }
  }

  private static boolean existsLogConfiguration(String configurationFile) {
    final Path configurationPath = Paths.get(configurationFile);
    return Files.isRegularFile(configurationPath) && Files.isReadable(configurationPath);
  }

  private static void initializeLogging(Path configurationFile) {
    try (final InputStream inputStream = Files.newInputStream(configurationFile)) {
      final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
      if (loggerFactory instanceof LoggerContext) {
        final LoggerContext loggerContext = (LoggerContext) loggerFactory;
        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        configurator.doConfigure(inputStream);
      }
      log = LoggerFactory.getLogger(LogInitializer.class);
      log.info("Log-System initialized with Configuration File {}", configurationFile);
    } catch (IOException | JoranException e) {
      System.err.println("Error while initializing Logback with ConfigurationFile " + configurationFile);
      e.printStackTrace(System.err);
    }
  }
}
