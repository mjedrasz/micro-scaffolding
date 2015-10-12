package com.scaffold.sample.core.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
	
	@Basic
	private boolean deleted;
	
	private Person() {}

	private Person(Long id, String firstName, String lastName, boolean deleted) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.deleted = deleted;
	}
	
	public void delete() {
		Preconditions.checkArgument(!deleted);
		this.deleted = true;
	}
}
