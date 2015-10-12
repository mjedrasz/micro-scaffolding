package com.scaffold.sample.rest.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.hateoas.ResourceSupport;

import com.scaffold.sample.core.domain.Person;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PersonResource extends ResourceSupport implements Serializable {

	public PersonResource(Person person) {
		this(person.getFirstName(), person.getLastName());
	}

	private String firstName;

	private String lastName;

}
