package com.scaffold.support.eurekadiscover.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.scaffold.support.eurekadiscover.app.OpenamDiscoveryApplication;

@SpringBootApplication(exclude = {LiquibaseAutoConfiguration.class, JmxAutoConfiguration.class})
@EnableEurekaClient
public class OpenamDiscoveryApplication {

	public static void main(String... args) {
		new SpringApplicationBuilder(OpenamDiscoveryApplication.class)
//        .web(false)
        .run(args);
	}

}