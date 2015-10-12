package com.scaffold.sample.core.application.command;

import lombok.Value;

@Value
public class CreatePersonCommand {

	private String firstName;
	private String lastName;
}
