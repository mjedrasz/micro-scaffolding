package com.scaffold.commons.test.category

public interface TestCategory {
	interface Fast {}
	interface Unit extends Fast {}
	interface Slow {}
	interface Performance extends Slow {}
	interface Database extends Slow {}
}