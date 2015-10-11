package com.scaffold.sample.core.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface PersonRepository extends Repository<Person, Long> {

	Optional<Person> findOne(Long id);
	
	Stream<Person> findAll();
	
	List<Person> findByFirstName(String firstName);
	
	Person save(Person person);
	
	Page<Person> findAll(Pageable pageable);
}
