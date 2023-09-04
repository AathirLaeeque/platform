package com.leucine.streem.migration.properties.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
  private final Properties properties;

  public PropertyLoader() {
    this.properties = new Properties();

    try (InputStream input = new FileInputStream("application.properties")) {
      properties.load(input);

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public String getProperty(String key) {
    return this.properties.getProperty(key);
  }
}
