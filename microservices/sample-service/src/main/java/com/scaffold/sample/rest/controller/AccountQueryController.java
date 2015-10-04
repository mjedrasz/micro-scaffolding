package com.scaffold.sample.rest.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.scaffold.sample.core.application.AccountService;
import com.scaffold.sample.rest.assembler.AccountResourceAssembler;
import com.scaffold.sample.rest.domain.AccountResource;
import com.scaffold.sample.shared.data.AccountDto;

@RestController
@RequestMapping("/api/sample/accounts")
public class AccountQueryController {

	private final AccountResourceAssembler accountResourceAssembler;
	private final AccountService accountService;

	@Inject
	public AccountQueryController(AccountService accountService, AccountResourceAssembler accountResourceAssembler) {
		this.accountResourceAssembler = checkNotNull(accountResourceAssembler);
		this.accountService = checkNotNull(accountService);
	}

	List<AccountDto> accounts = Arrays.asList(new AccountDto("user1", "u1@mikro.pl"), new AccountDto("user2",
			"u2@mikro.pl"));

	@Timed
	@RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json" })
	public HttpEntity<PagedResources<AccountResource>> getAccounts(
			@PageableDefault(size = 100, page = 0) Pageable pageable, PagedResourcesAssembler<AccountDto> assembler) {
		accountService.doIt();
		Page<AccountDto> accountNamesPage = new PageImpl<>(accounts, pageable, accounts.size());

		PagedResources<AccountResource> pagedResource = assembler
				.toResource(accountNamesPage, accountResourceAssembler);
		return new ResponseEntity<>(pagedResource, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/{login}", method = RequestMethod.GET, produces = { "application/hal+json" })
	public ResponseEntity<AccountResource> getAccountByLogin(@PathVariable String login) {
		//@formatter:off
		accountService.doIt();
		return accounts.stream()
				.filter(a -> a.getLogin().equals(login)).findFirst()
				.map(a -> new ResponseEntity<>(accountResourceAssembler.toResource(a), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		//@formatter:on
	}
}
