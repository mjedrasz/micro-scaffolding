package com.scaffold.sample.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

@RestController
@RequestMapping("/api/sample")
public class IndexController {

	@Timed
	@RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
	public ResponseEntity<ResourceSupport> index() {
		ResourceSupport indexResource = new ResourceSupport();
		indexResource.add(linkTo(methodOn(PersonQueryController.class).allPeople(null, null)).withRel("people"));
		return new ResponseEntity<>(indexResource, HttpStatus.OK);
	}
}
