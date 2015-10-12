package com.scaffold.sample.app;

import javax.inject.Inject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;

import com.scaffold.sample.core.domain.PersonRepository;
import com.scaffold.sample.rest.config.SampleRestConfig;

@SpringCloudApplication
@Import(SampleRestConfig.class)
public class SampleApplication implements CommandLineRunner {
   
	@Inject 
	PersonRepository presonRepository;
	
	@Override
	public void run(String... args) throws Exception {
		System.err.println(this.presonRepository.streamAll());
	}
 
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleApplication.class, args);
	}
}