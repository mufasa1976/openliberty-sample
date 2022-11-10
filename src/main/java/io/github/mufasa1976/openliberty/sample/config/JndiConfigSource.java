package io.github.mufasa1976.openliberty.sample.config;

import io.github.mufasa1976.openliberty.sample.JndiConstants;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.removeEndIgnoreCase;

@Slf4j
public class JndiConfigSource implements ConfigSource {
  private static final String JNDI_NAME_CONFIG_DIRECTORY = JndiConstants.CONFIGURATION_DIRECTORY;

  private static final String JNDI_NAME_CONFIG_FILE = JndiConstants.CONFIGURATION_FILE;
  private final String configDirectory;

  private final String configFile;
  private final Map<String, String> properties = new HashMap<>();

  public JndiConfigSource() {
    this.configDirectory = getJndiEntryValue(JNDI_NAME_CONFIG_DIRECTORY);
    this.configFile = getJndiEntryValue(JNDI_NAME_CONFIG_FILE);
    getConfigResourceBundle().ifPresent(resourceBundle -> {
      properties.clear();
      for (Enumeration<String> keys = resourceBundle.getKeys(); keys.hasMoreElements(); ) {
        final String key = keys.nextElement();
        properties.put(key, resourceBundle.getString(key));
      }
    });
  }

  private String getJndiEntryValue(String jndiEntryKey) {
    try {
      return InitialContext.doLookup(jndiEntryKey);
    } catch (NamingException e) {
      log.warn("Cannot get Value of JNDI Entry {}", jndiEntryKey);
      return null;
    }
  }

  private Optional<ResourceBundle> getConfigResourceBundle() {
    if (configDirectory == null || configFile == null) {
      return Optional.empty();
    }

    try {
      final ClassLoader configDirectoryClassLoader =
          new URLClassLoader(new URL[] {Paths.get(configDirectory).toUri().toURL()});
      return Optional.of(
          ResourceBundle.getBundle(removeEndIgnoreCase(configFile, ".properties"), Locale.getDefault(), configDirectoryClassLoader));
    } catch (MalformedURLException e) {
      log.warn("Cannot create URL-Classloader out of Directory {}", configDirectory);
      return Optional.empty();
    } catch (MissingResourceException e) {
      log.warn("Resource Bundle {} wouldn't exist within Directory {}", configFile, configDirectory);
      return Optional.empty();
    }
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public int getOrdinal() {
    return 150;
  }

  @Override
  public Set<String> getPropertyNames() {
    return properties.keySet();
  }

  @Override
  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(properties);
  }

  @Override
  public String getValue(String s) {
    return properties.get(s);
  }
}
