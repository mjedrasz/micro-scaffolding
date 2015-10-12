package com.scaffold.sample.rest.assembler;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.scaffold.sample.core.domain.Person;
import com.scaffold.sample.rest.controller.PersonQueryController;
import com.scaffold.sample.rest.domain.PersonResource;

@Component
public class PersonResourceAssembler extends ResourceAssemblerSupport<Person, PersonResource> {

	public PersonResourceAssembler() {
		super(PersonQueryController.class, PersonResource.class);
	}

	@Override
	public PersonResource toResource(Person entity) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
