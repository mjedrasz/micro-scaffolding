package com.scaffold.sample.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.scaffold.sample.core.domain.Person;
import com.scaffold.sample.rest.controller.PersonCommandController;
import com.scaffold.sample.rest.controller.PersonQueryController;
import com.scaffold.sample.rest.domain.PersonResource;

@Component
public class PersonResourceAssembler extends ResourceAssemblerSupport<Person, PersonResource> {

	public PersonResourceAssembler() {
		super(PersonQueryController.class, PersonResource.class);
	}

	@Override
	public PersonResource toResource(Person person) {
		PersonResource personResource = new PersonResource(person);
		personResource.add(linkTo(methodOn(PersonCommandController.class).deletePerson(person.getId())).withRel(
				"delete"));
		return personResource;
	}

}
