package com.scaffold.sample.rest.config;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.Module;
import com.scaffold.common.rest.config.CommonRestConfig;
import com.scaffold.common.rest.databind.MixinModuleFactory;
import com.scaffold.sample.core.config.SampleConfig;

@Configuration
@ComponentScan(basePackages = { "com.scaffold.sample.rest" })
@Import({ CommonRestConfig.class, SampleConfig.class })
public class SampleRestConfig extends WebMvcConfigurerAdapter {

	@Bean
   Module mixin() {
	   Map<Class<?>, Class<?>> mixins = new HashMap<>();
       mixins.put(Link.class, MyLinkMixin.class);
       return MixinModuleFactory.createMixinModule(mixins);
   }
	
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder(ApplicationContext ctx) {
		Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
		b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		b.applicationContext(ctx);
		return b;
	}
	
	
}
