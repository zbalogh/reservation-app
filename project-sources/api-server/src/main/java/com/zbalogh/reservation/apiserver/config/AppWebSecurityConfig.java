package com.zbalogh.reservation.apiserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.zbalogh.reservation.apiserver.auth.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity
class AppWebSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@SuppressWarnings("deprecation")
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return NoOpPasswordEncoder.getInstance();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception
	{
		//httpSecurity.authorizeRequests().anyRequest().authenticated()
        //.and().httpBasic();
		
		// disable CSRF
		httpSecurity.csrf().disable()
		
		// enable CORS
		.cors().and()
		
		// configure headers
		// https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/headers.html
		.headers()
			.cacheControl().and()
			.contentTypeOptions().and()
			.httpStrictTransportSecurity().and()
			.xssProtection().and()
			.frameOptions().sameOrigin()
		.and()
		
		/*
		// enable access with authentication for these pages
		.authorizeRequests()
			.antMatchers(AppConstants.RESTAPI_BASE_URL + "/hello").permitAll()
			.antMatchers(AppConstants.RESTAPI_BASE_URL + "/health").permitAll()
			
			//.antMatchers(AppConstants.RESTAPI_BASE_URL + "/data/**").authenticated()
			//.antMatchers(HttpMethod.DELETE, AppConstants.RESTAPI_BASE_URL + "/data/**").authenticated()
			//.antMatchers(HttpMethod.PUT, AppConstants.RESTAPI_BASE_URL + "/data/**").authenticated()
			
			.antMatchers(AppConstants.RESTAPI_BASE_URL + "/data/**").permitAll()
			.antMatchers("/").permitAll()
		
		// but all other URLs is protected by authentication
		.anyRequest().authenticated()
		.and()
		*/
		
		// we will handle the authorization directly in the place where we need it (in the controller layer)
		// because we need authorization only in some REST methods, not in all service URLS, etc.
		.authorizeRequests()
			.anyRequest().permitAll()
		.and()
		
		// configure the exception handling
		.exceptionHandling();
						
		// configure the session management: use STATELESS policy because of JWT based authorization requires this policy
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		// add JWT request filter to handle JWT authorization: JWT token sent by the client via request header
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
