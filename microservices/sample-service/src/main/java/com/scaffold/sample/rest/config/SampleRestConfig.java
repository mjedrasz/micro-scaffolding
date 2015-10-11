package com.scaffold.sample.rest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
 
import com.scaffold.common.rest.config.CommonRestConfig;
import com.scaffold.sample.core.config.SampleConfig;

@Configuration
@ComponentScan(basePackages = { "com.scaffold.sample.rest" })
@Import({ CommonRestConfig.class, SampleConfig.class })
public class SampleRestConfig extends WebMvcConfigurerAdapter {

   
}
