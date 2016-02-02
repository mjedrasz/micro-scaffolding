package com.scaffold.sample.app;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< Updated upstream
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
=======
>>>>>>> Stashed changes
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.scaffold.sample.core.config.SwaggerConfig;
import com.scaffold.sample.core.domain.PersonRepository;
import com.scaffold.sample.rest.config.SampleRestConfig;

<<<<<<< Updated upstream
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
//@EnableOAuth2Sso
@EnableDiscoveryClient
@Import(SampleRestConfig.class)
=======
@SpringBootApplication
@Import({SampleRestConfig.class, SwaggerConfig.class})
>>>>>>> Stashed changes
public class SampleApplication implements CommandLineRunner {

	@Inject
	PersonRepository presonRepository;

	@Override
	public void run(String... args) throws Exception {
		System.err.println(this.presonRepository.findAll());
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleApplication.class, args);
	}

	@Configuration
	protected static class SecurityConfiguration extends OAuth2SsoConfigurerAdapter {

		@Override
		public void match(RequestMatchers matchers) {
			matchers.anyRequest();
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/api/**", "/index.html", "/home.html", "/").permitAll().anyRequest()
					.authenticated().and().csrf().disable();
			// .csrfTokenRepository(csrfTokenRepository()).and()
			// .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}

		private Filter csrfHeaderFilter() {
			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
						FilterChain filterChain) throws ServletException, IOException {
					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
					if (csrf != null) {
						Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
						String token = csrf.getToken();
						if (cookie == null || token != null && !token.equals(cookie.getValue())) {
							cookie = new Cookie("XSRF-TOKEN", token);
							cookie.setPath("/");
							response.addCookie(cookie);
						}
					}
					filterChain.doFilter(request, response);
				}
			};
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}
	}

//	public static void play() throws IOException {
//		OkHttpClient client = new OkHttpClient();
//		client.interceptors().add(new Interceptor() {
//		    @Override
//		    public Response intercept(Chain chain) throws IOException {
//		    	chain.request().newBuilder().url(url)
//		        return null;
//		    }
//		});
//		Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com").build();
//
//		MyService service = retrofit.create(MyService.class);
//		Call<Test> it = service.getIt("");
//		it.execute();
//
//	}
//
//	public static interface MyService {
//		@GET
//		Call<Test> getIt(@Url String url);
//
//	}
//
//	private static class Test {
//		private String x;
//	}
}