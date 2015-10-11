package com.scaffold.sample.core.domain

import spock.lang.Specification

import com.scaffold.commons.test.category.Fast

@Fast
class TestSpec extends Specification {

	def "test"() {
		given:
		def x = 1
		when:
		x = 0
		then:
		x == 0
	}
}
