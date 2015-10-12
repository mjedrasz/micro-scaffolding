package com.scaffold.sample.rest.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.hateoas.ResourceSupport;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonResource extends ResourceSupport implements Serializable {

	private String firstName;

	private String lastName;

}
