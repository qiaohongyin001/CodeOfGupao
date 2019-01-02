package com.wolfman.es.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:es-config.properties")
@Data
public class EsConfig {

  @Value("${es.cluster.name}")
  private String clusterName;

  @Value("${es.host.ip}")
  private String ip;

  @Value("${es.host.port}")
  private int port;

}
