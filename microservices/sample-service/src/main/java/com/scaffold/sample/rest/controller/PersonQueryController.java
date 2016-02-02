package com.scaffold.sample.rest.controller;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.scaffold.sample.core.application.PersonService;
import com.scaffold.sample.core.domain.Person;
import com.scaffold.sample.rest.assembler.PersonResourceAssembler;
import com.scaffold.sample.rest.config.View;
import com.scaffold.sample.rest.domain.PersonResource;

@RestController
@RequestMapping("/api/people")
public class PersonQueryController {

	private final PersonResourceAssembler personResourceAssembler;
	private final PersonService personService;
	private final RestTemplate restTemplate;

	@Inject
	public PersonQueryController(PersonResourceAssembler personResourceAssembler, PersonService personService) {
		this.personResourceAssembler = checkNotNull(personResourceAssembler);
		this.personService = checkNotNull(personService);
		this.restTemplate = new RestTemplate();
	}

	@Timed
	@RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" }, params = { "firstName" })
	public HttpEntity<List<PersonResource>> allPeopleByFirstName(@RequestParam("firstName") String firstName) {

		List<Person> people = personService.findByFirstName(firstName);

		return new ResponseEntity<>(personResourceAssembler.toResources(people), HttpStatus.OK);
	}

	@Timed
    @JsonView(View.Other.class)
	@RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
	public HttpEntity<PagedResources<PersonResource>> allPeople(
			@PageableDefault(size = 100, page = 0) Pageable pageable, PagedResourcesAssembler<Person> assembler) {

		PagedResources<PersonResource> pagedResources = assembler.toResource(personService.findAll(pageable),
				personResourceAssembler);
		pagedResources.add(linkTo(methodOn(PersonCommandController.class).createPerson(null)).withRel("create"));
		return new ResponseEntity<>(pagedResources, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/hal+json" })
	public ResponseEntity<PersonResource> personById(@PathVariable Long id) {
		try {
		PersonResource forObject = restTemplate.getForObject("http://localhost:7475/api/people/abc", PersonResource.class);
		System.out.println(forObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//@formatter:off
		return personService.findById(id)
				.map(person -> new ResponseEntity<>(personResourceAssembler.toResource(person),	HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		//@formatter:on
	}
	@Timed
	@RequestMapping(value = "/abc", method = RequestMethod.GET, produces = { "application/hal+json" })
	public ResponseEntity<PersonResource> personById2() {
				//@formatter:off
				return new ResponseEntity<>(new PersonResource("f", "l"), HttpStatus.OK);
				//@formatter:on
	}
}
