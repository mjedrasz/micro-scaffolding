package com.scaffold.sample.core.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = { "com.scaffold.sample.core" })
@EnableJpaRepositories("com.scaffold.sample.core.domain")
@EntityScan("com.scaffold.sample.core.domain")
@EnableTransactionManagement
@EnableAutoConfiguration
public class SampleConfig {

   
}
