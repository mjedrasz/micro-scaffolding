package com.scaffold.sample.core.domain.fixture

import com.scaffold.sample.core.domain.Person;

class PersonFixture {
	 
	private static person(id) {
		Person.builder().id(id).firstName("first name $id").lastName("last name $id").build()
	}
}
