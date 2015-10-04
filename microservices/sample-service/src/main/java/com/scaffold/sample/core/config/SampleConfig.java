package com.scaffold.sample.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.scaffold.common.rest.config.CommonRestConfig;

@Configuration
@ComponentScan(basePackages = { "com.scaffold.sample.core" })
public class SampleConfig {

   
}
