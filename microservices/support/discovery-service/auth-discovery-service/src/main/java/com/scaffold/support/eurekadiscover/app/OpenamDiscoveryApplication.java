package com.scaffold.support.eurekadiscover.app;

import io.vertx.core.Vertx;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import com.scaffold.support.eurekadiscover.app.sandbox.StaticServer;

@SpringBootApplication(exclude = {LiquibaseAutoConfiguration.class, JmxAutoConfiguration.class})
public class OpenamDiscoveryApplication {

	 @Autowired
	  private StaticServer staticServer;

	 
	public static void main(String... args) {
		new SpringApplicationBuilder(OpenamDiscoveryApplication.class)
		.web(false)
        .run(args);
	}
	
	@Bean
	InitializingBean deployVerticle() {
		return () -> {
	    Vertx.vertx().deployVerticle(staticServer);
	  };
	}

}