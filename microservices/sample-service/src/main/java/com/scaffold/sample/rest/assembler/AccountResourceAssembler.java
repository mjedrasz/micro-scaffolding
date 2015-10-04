package com.scaffold.sample.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.scaffold.sample.rest.controller.AccountQueryController;
import com.scaffold.sample.rest.domain.AccountResource;
import com.scaffold.sample.shared.data.AccountDto;

@Component
public class AccountResourceAssembler implements
		ResourceAssembler<AccountDto, AccountResource> {

	@Override
	public AccountResource toResource(AccountDto account) {
		AccountResource accountResource = new AccountResource(account);
		accountResource.add(linkTo(
				methodOn(AccountQueryController.class).getAccountByLogin(
						account.getLogin())).withSelfRel());
		return accountResource;
	}
}
