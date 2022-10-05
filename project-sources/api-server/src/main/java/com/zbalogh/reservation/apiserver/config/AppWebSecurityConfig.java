package com.zbalogh.reservation.apiserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;

import com.zbalogh.reservation.apiserver.auth.jwt.CustomJwtAuthenticationManager;
import com.zbalogh.reservation.apiserver.auth.jwt.CustomJwtServerAuthenticationConverter;

import reactor.core.publisher.Mono;

//
// https://docs.spring.io/spring-security/reference/reactive/configuration/webflux.html
//
// https://www.baeldung.com/spring-security-5-reactive
//

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class AppWebSecurityConfig
{
	//@Autowired
	//protected JwtRequestFilter jwtRequestFilter;

	//@Autowired
	//protected CustomJwtAuthenticationManager authenticationManager;
	
	//@Autowired
	//protected CustomJwtServerAuthenticationConverter serverAuthenticationConverter;
	
	//@SuppressWarnings("deprecation")
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		//return NoOpPasswordEncoder.getInstance();
		return new PasswordEncoder()
		{
			@Override
			public String encode(CharSequence rawPassword)
			{
				return rawPassword.toString();
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword)
			{
				return rawPassword.toString().equals(encodedPassword);
			}
		};
	}
	
	//
	// https://www.youtube.com/watch?v=wyl06YqMxaU&ab_channel=AlexGutjahr
	// https://youtu.be/wyl06YqMxaU?t=1499
	//
	
	@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
    		CustomJwtAuthenticationManager authenticationManager,
    		CustomJwtServerAuthenticationConverter serverAuthenticationConverter)
	{
		// disable CSRF
		http.csrf().disable()
		
		// enable CORS
		.cors().and()
		
		// configure headers
		// https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/headers.html
		.headers()
			.cache(c -> {})
			.contentTypeOptions(c -> {})
			.hsts(c -> {})
			.xssProtection(c -> {})
			.frameOptions().mode(Mode.SAMEORIGIN)
		.and()
		
		/*
		.authorizeExchange()
			.pathMatchers(AppConstants.RESTAPI_BASE_URL + "/hello").permitAll()
			.pathMatchers(AppConstants.RESTAPI_BASE_URL + "/health").permitAll()
			.pathMatchers(HttpMethod.DELETE, AppConstants.RESTAPI_BASE_URL + "/data/**").authenticated()
			.pathMatchers(HttpMethod.PUT, AppConstants.RESTAPI_BASE_URL + "/data/**").authenticated()
			.pathMatchers(AppConstants.RESTAPI_BASE_URL + "/data/**").permitAll()
			.pathMatchers("/").permitAll()
			.anyExchange().authenticated()
		.and()
		*/
		
		// we will handle the authorization directly in the place where we need it (in the REST controller)
		// because we need authorization only in some REST methods, not in all service URLS, etc.
		.authorizeExchange()
			.anyExchange().permitAll()
		.and()
		
		// disable BASIC and FORM authentication
		.httpBasic().disable()
		.formLogin().disable()
		
		// configure the exception handling
		.exceptionHandling()
		
		// whenever the application requires authentication (when client request a protected page without authorization token),
		// then we responses 401 UNAUTHORIZED with "Bearer"
		.authenticationEntryPoint( (swe, e) -> 
        	Mono.fromRunnable(() -> {
        		swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        		swe.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        	})
		)
		
		// whenever the access is denied, then we response 403 FORBIDDEN
		.accessDeniedHandler((swe, e) -> 
        	Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
		);
		
		// configure the session management: use STATELESS policy because of JWT based authorization requires this policy
		http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
		
		// add JWT request filter to handle JWT authorization: JWT token sent by the client via request header
		//http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		//http.addFilterBefore(jwtRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION);
		
		// add AuthenticationWebFilter with my custom AuthManager and AuthConverter
		AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authenticationManager);
		authFilter.setServerAuthenticationConverter(serverAuthenticationConverter);
		http.addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION);
		
		return http.build();
    }
	
}
