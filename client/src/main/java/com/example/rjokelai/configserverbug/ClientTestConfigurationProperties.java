package com.example.rjokelai.configserverbug;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix = "client")
public class ClientTestConfigurationProperties {

  private Map<String, Map<String, String>> mapFromProperties = new HashMap<>();
  private Map<String, Map<String, String>> mapFromYaml = new HashMap<>();

  public Map<String, Map<String, String>> getMapFromYaml() {
    return mapFromYaml;
  }

  public Map<String, Map<String, String>> getMapFromProperties() {
    return mapFromProperties;
  }

  public void setMapFromProperties(Map<String, Map<String, String>> mapFromProperties) {
    this.mapFromProperties = mapFromProperties;
  }

  public void setMapFromYaml(Map<String, Map<String, String>> mapFromYaml) {
    this.mapFromYaml = mapFromYaml;
  }

}
