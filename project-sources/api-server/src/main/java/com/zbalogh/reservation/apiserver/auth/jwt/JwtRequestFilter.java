package com.zbalogh.reservation.apiserver.auth.jwt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zbalogh.reservation.apiserver.grpc.services.UserGrpcService;
import com.zbalogh.reservation.apiserver.grpc.stub.UserGRPC.UserResponse;

import reactor.core.publisher.Mono;

// https://www.baeldung.com/spring-webflux-filters
//
// https://stackoverflow.com/questions/59289029/how-to-implement-migrate-onceperrequestfilter-using-spring-webflux
//
// https://github.com/jwtk/jjwt

//@Component
@Deprecated
public class JwtRequestFilter implements WebFilter
{
	private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserGrpcService userGrpcService;
	
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
	{
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		
		// execute my filter logic
		Authentication authentication = doMyFilter(request, response);
		
		// continue with other chains
		if (authentication != null) {
			//return chain.filter(exchange).subscriberContext(c -> ReactiveSecurityContextHolder.withAuthentication(authentication));
			return chain.filter(exchange).contextWrite(context -> {
				return ReactiveSecurityContextHolder.withAuthentication(authentication);
			});
		}
		else {
			return chain.filter(exchange);
		}
	}
	
	private Authentication doMyFilter(ServerHttpRequest request, ServerHttpResponse response)
	{
		logger.info("JwtRequestFilter(): running JWT authorization filter.");
		
		// get the Authorization from the request header
		final String authorizationHeader = request.getHeaders().getFirst("Authorization");

		//
		// if we have Bearer Authorization header then we parse and validate the JWT token
		//
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
		{
			String username = null;
			String jwt = null;
			
			try {
				// get the token
				jwt = authorizationHeader.substring(7);

				// extract user name from the token
				username = jwtUtil.extractUsername(jwt);

				logger.info("JwtRequestFilter(): The username is extracted from the JWT token. | username=" + username);

				//
				// if we have user name from token then we add authentication object after validating the token.
				//
				if (username != null)
				{
					// validate JWT token
					//if ( jwtUtil.validateToken(jwt) && checkUserExistsOnAuthServer(username) )
					if (jwtUtil.validateToken(jwt))
					{
						// token is valid
						// create UserDetails object for the given user name
						UserDetails userDetails = createUserDetailsObject(username);

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

						logger.info("JwtRequestFilter(): JWT token is valid, and authorization is successful. | Authentication: " + usernamePasswordAuthenticationToken);
						
						return usernamePasswordAuthenticationToken;
					}
				}
			}
			catch (Exception ex) {
				// if we have any exception, just we catch it
				logger.error("Exception while parsing and validating JWT token.", ex);
				
				// in that case there will be no authentication in the security context
				// The response status is 401 - Unauthorized
				//response.setStatus(HttpStatus.UNAUTHORIZED.value());
				//
				// !!! Spring Security will refuse the request with 401 or 403 status code because no authentication object will be found in the security context. !!! 
			}
		}
		
		// if we are here, then no authentication, return null
		return null;
	}

	/**
	 * JWT token is valid, so create an UserDetails object with the given username.
	 * 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	private UserDetails createUserDetailsObject(String username)
	{
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		return new User(username, "", authorities);
	}
	
	/**
	 * Check the given user on the Authentication Server via GRPC.
	 * 
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkUserExistsOnAuthServer(String username)
	{
		boolean userExists = false;
		
		try {
			UserResponse response = userGrpcService.findUserByName(username);
			
			if (response != null) {
				logger.info("User '" + username + "' exists on the Authentication Server. | " + response.getFirstname() + " " + response.getLastname());
				userExists = true;
			}
			else {
				logger.info("User '" + username + "' does not exist on the Authentication Server.");
				userExists = false;
			}
		}
		catch (Exception ex) {
			logger.error("Exception while checking user '" + username + "' on the authentication server via GRPC.", ex);
			// if authentication server is not available or any error occurs then we return true
			// actually the JWT token is validated before executing this method, so should not be big problem to return true in that case.
			// NOTE: We are thinking and looking for better solution...
			userExists = true;
		}
		
		return userExists;
	}

}
