package com.zbalogh.reservation.apiserver.auth.jwt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Custom JWT Authentication Manager.
 * 
 * It retrieves the @Authentication object. If it is @BearerAuthenticationToken type, then it execute the validation and authorization.
 * As a result if JWT token valid, then it responses @UsernamePasswordAuthenticationToken with the username and authorities.
 * Otherwise @InvalidBearerToken exception is thrown.
 * 
 * @author ZBalogh
 *
 */
@Component
public class CustomJwtAuthenticationManager implements ReactiveAuthenticationManager
{
	private static final Logger logger = LoggerFactory.getLogger(CustomJwtAuthenticationManager.class);
	
	@Autowired
	private JwtUtil jwtUtil;
	
	
    @Override
    public Mono<Authentication> authenticate(Authentication authentication)
    {
    	// validate the given JWT Bearer token (provided by our server authentication converter)
    	// if the token is valid, we return Authentication object with the username (extracted from JWT) and Authorities
    	// otherwise we return authentication error
        return Mono.justOrEmpty(authentication)
        		.filter(auth -> auth instanceof BearerAuthenticationToken)
        		.cast(BearerAuthenticationToken.class)
        		.flatMap(token -> Mono.justOrEmpty(validateJwtToken(token)))
        		.onErrorMap(ex -> new InvalidBearerToken(ex.getMessage(), ex));
    }
    
    /**
     * Validate the given JWT Bearer token, and extract the username from the token.
     * If JWT token is valid then it returns Authentication. Otherwise it throws Exception.
     * 
     * @param token
     * @return
     */
    private Authentication validateJwtToken(BearerAuthenticationToken token)
    {
		// get JWT from the Bearer token
		String jwt = token.getToken();

		// extract user name from the token
		String username = jwtUtil.extractUsername(jwt);

		logger.info("The username is extracted from the JWT token. | username=" + username);
		
		// validate JWT token
		if (jwtUtil.validateToken(jwt))
		{
			UserDetails userDetails = createUserDetailsObject(username);

			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

			logger.info("JWT token is valid, and authorization is successful. | Authentication: " + usernamePasswordAuthenticationToken);
			
			return usernamePasswordAuthenticationToken;
		}
		
    	throw new IllegalArgumentException("JWT Token is not valid.");
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
	
}
