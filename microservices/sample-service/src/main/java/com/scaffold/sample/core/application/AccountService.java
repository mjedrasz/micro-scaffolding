package com.scaffold.sample.core.application;

import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class AccountService {

	@HystrixCommand(fallbackMethod = "doOther")
	public void doIt() {
		if (((int)(Math.random()) * 100) % 3 == 0) {
			throw new RuntimeException();
		}
	}
	
	public void doOther() {
		
	}
}
