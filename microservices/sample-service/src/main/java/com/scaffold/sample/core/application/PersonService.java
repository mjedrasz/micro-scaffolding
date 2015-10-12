package com.scaffold.sample.core.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.scaffold.sample.core.application.command.CreatePersonCommand;
import com.scaffold.sample.core.application.command.DeletePersonCommand;
import com.scaffold.sample.core.domain.Person;
import com.scaffold.sample.core.domain.PersonRepository;

@Service
@Transactional
public class PersonService {

	private final PersonRepository personRepository;

	@Inject
	public PersonService(PersonRepository personRepository) {
		this.personRepository = Preconditions.checkNotNull(personRepository);
	}

	public Optional<Person> findById(Long id) {
		return personRepository.findOne(id);
	}

	public Stream<Person> findAll() {
		return personRepository.findAll();
	}

	public List<Person> findByFirstName(String firstName) {
		return personRepository.findByFirstName(firstName);
	}

	public Person create(CreatePersonCommand command) {
		Preconditions.checkArgument(command != null);
		return personRepository.save(Person.builder().firstName(command.getFirstName()).lastName(command.getLastName())
				.build());
	}

	public void delete(DeletePersonCommand command) {
		Preconditions.checkArgument(command != null);
		Preconditions.checkArgument(command.getId() != null);

		Optional<Person> person = personRepository.findOne(command.getId());
		person.ifPresent(p -> {
			p.delete();
			personRepository.save(p);
		});
	}

	public Page<Person> findAll(Pageable pageable) {
		return personRepository.findAll(pageable);
	}
}
