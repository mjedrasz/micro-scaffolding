package com.scaffold.commons.async.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebAsyncSupportConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	@Qualifier("webAsyncExecutor")
	private AsyncTaskExecutor taskExecutor;

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor(taskExecutor).setDefaultTimeout(10000);
	}

	@Configuration
	static class AsyncTaskExecutorConfiguration {

		@Bean(name = "webAsyncExecutor")
		public Executor getAsyncExecutor() {
			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(2);
			executor.setMaxPoolSize(50);
			executor.setQueueCapacity(10000);
			executor.setThreadNamePrefix("mvc-async-executor-");
			return new ExceptionHandlingAsyncTaskExecutor(executor);
		}
	}
}
