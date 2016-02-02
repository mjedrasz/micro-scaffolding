package com.scaffold.sample.rest.controller;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.scaffold.sample.core.application.PersonService;
import com.scaffold.sample.core.application.command.CreatePersonCommand;
import com.scaffold.sample.core.application.command.DeletePersonCommand;
import com.scaffold.sample.core.domain.Person;
import com.scaffold.sample.rest.assembler.PersonResourceAssembler;
import com.scaffold.sample.rest.domain.PersonResource;

@RestController
@RequestMapping("/api/jpeople")
public class PersonCommandController {

	private final PersonResourceAssembler personResourceAssembler;
	private final PersonService personService;

	@Inject
	public PersonCommandController(PersonResourceAssembler personResourceAssembler, PersonService personService) {
		this.personResourceAssembler = checkNotNull(personResourceAssembler);
		this.personService = checkNotNull(personService);
	}

	@Timed
	@RequestMapping(method = RequestMethod.POST, produces = { "application/hal+json" })
	public HttpEntity<PersonResource> createPerson(@RequestBody PersonResource person) {

		Person createdPerson = personService
				.create(new CreatePersonCommand(person.getFirstName(), person.getLastName()));
		HttpHeaders httpHeaders = headers(createdPerson);
		return new ResponseEntity<>(personResourceAssembler.toResource(createdPerson), httpHeaders, HttpStatus.CREATED);
	}

	@Timed
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = { "application/hal+json" })
	public HttpEntity<Void> deletePerson(@PathVariable("id") Long id) {

		personService.delete(new DeletePersonCommand(id));
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private HttpHeaders headers(Person createdPerson) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders
				.setLocation(linkTo(methodOn(PersonQueryController.class).personById(createdPerson.getId())).toUri());
		return httpHeaders;
	}

}
