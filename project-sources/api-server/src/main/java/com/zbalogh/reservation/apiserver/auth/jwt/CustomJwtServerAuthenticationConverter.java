package com.zbalogh.reservation.apiserver.auth.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Custom JWT server authentication converter. 
 * It converts the Bearer token (JWT) from the HTTP request header into @BearerAuthenticationToken object.
 * 
 * @author ZBalogh
 *
 */
@Component
public class CustomJwtServerAuthenticationConverter implements ServerAuthenticationConverter
{

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange)
	{
		// read 'Authorization' header from the request
		String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		
		// create Mono with the given authorization header value or empty mono if header is null
		// convert/map the token value into "BearerAuthenticationToken" object.
		// NOTE: The "CustomJwtAuthenticationManager" will get this object as input and it will carry out the token validation, etc.
		return Mono.justOrEmpty(authorizationHeader)
				.filter(s -> s.startsWith("Bearer"))
				.map(s -> s.substring(7))
				.map(jwt -> new BearerAuthenticationToken(jwt));
	}

}
