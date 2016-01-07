package com.scaffold.support.zuul.app;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonQueryController {
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String ok() {
		return "ok";
	}
}
