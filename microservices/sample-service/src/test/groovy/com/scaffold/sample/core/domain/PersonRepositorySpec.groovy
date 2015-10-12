package com.scaffold.sample.core.domain

import java.util.stream.Collectors

import javax.inject.Inject

import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import com.scaffold.commons.test.category.Database
import com.scaffold.commons.test.category.Fast
import com.scaffold.commons.test.category.Slow
import com.scaffold.commons.test.dbunit.DbUnitSpec
import com.scaffold.sample.core.config.SampleConfig
import com.scaffold.sample.core.domain.dataset.PersonDataset
import com.scaffold.sample.core.domain.fixture.PersonFixture
import com.shazam.shazamcrest.matcher.Matchers

@ContextConfiguration(classes = SampleConfig.class)
@Transactional
class PersonRepositorySpec extends DbUnitSpec {

	@Inject
	private PersonRepository repository

	@Database
	def "Should return one person"() {
		given:
		dataSet PersonDataset.personDataset
		when:
		def person = repository.findOne(1l)

		then:
		person.isPresent()
		(person.get()) Matchers.sameBeanAs(PersonFixture.person(1))
	}
	@Slow
	def "Should return all people"() {
		given:
		dataSet PersonDataset.personDataset
		when:
		def people = repository.streamAll()

		then:
		def peopleList = people.collect(Collectors.toList())
		peopleList.size() == 4
		peopleList org.hamcrest.Matchers.hasItems(
				Matchers.sameBeanAs(PersonFixture.person(1)),
				Matchers.sameBeanAs(PersonFixture.person(2)),
				Matchers.sameBeanAs(PersonFixture.person(3)),
				Matchers.sameBeanAs(PersonFixture.person(4))
		)
	}

	@Fast
	def "Should return all people by first name"() {
		given:
		dataSet PersonDataset.personDataset
		when:
		def peopleList = repository.findByFirstName('first name 1')

		then:
		peopleList.size() == 1
		peopleList org.hamcrest.Matchers.hasItem(
				Matchers.sameBeanAs(PersonFixture.person(1))
		)
	}
	
	def "Should return page of people"() {
		given:
		dataSet PersonDataset.personDataset
		when:
		def page = repository.findAll(new PageRequest(0, 2))

		then:
		page.getTotalElements() == 4
		page.getNumberOfElements() == 2
		def peopleList = page.getContent()
		peopleList.size() == 2
	}
}
