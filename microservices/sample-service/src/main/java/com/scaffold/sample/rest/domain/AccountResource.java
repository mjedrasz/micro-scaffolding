package com.scaffold.sample.rest.domain;

import java.io.Serializable;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import com.scaffold.sample.shared.data.AccountDto;

@SuppressWarnings("serial")
public class AccountResource extends Resource<AccountDto> implements
		Serializable {

	public AccountResource(AccountDto content, Iterable<Link> links) {
		super(content, links);
	}

	public AccountResource(AccountDto content, Link... links) {
		super(content, links);
	}
}
