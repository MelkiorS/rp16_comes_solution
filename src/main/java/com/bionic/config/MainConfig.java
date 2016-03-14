package com.bionic.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = PersistenceConfig.class)
@ComponentScan(basePackages = {"com.bionic.dao", "com.bionic.model", "com.bionic.service"})
public class MainConfig {
}
