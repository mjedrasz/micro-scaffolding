package com.scaffold.sample.core.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Entity
@Builder
@ToString
@Getter
public class Person implements Serializable {

	@Id
	@GeneratedValue
	private Long id;
	
	@Basic
	private String firstName;
	@Basic
	private String lastName;
	
	private Person() {}

	private Person(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
