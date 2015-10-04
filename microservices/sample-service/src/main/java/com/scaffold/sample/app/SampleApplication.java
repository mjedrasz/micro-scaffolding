package com.scaffold.sample.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;

import com.scaffold.sample.rest.config.SampleRestConfig;

@SpringCloudApplication
@Import(SampleRestConfig.class)
public class SampleApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SampleApplication.class).run(args);
	}
}