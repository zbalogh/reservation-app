package com.zbalogh.reservation.apiserver.auth.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter
{
	private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException
	{
		// get the Authorization from the request header
		final String authorizationHeader = request.getHeader("Authorization");

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

				// get Authentication object from the security context
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

				logger.info("JwtRequestFilter(): The username is extracted from the JWT token. | username=" + username);

				//
				// if we have user name from token but no Authentication in the security context
				// then we add authentication object after validating the token.
				//
				if (username != null && authentication == null)
				{
					// validate JWT token
					if (jwtUtil.validateToken(jwt))
					{
						// token is valid
						// create UserDetails object for the given user name
						UserDetails userDetails = createUserDetailsObject(username);

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

						usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						// Set authentication object into the security context
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

						logger.info("JwtRequestFilter(): JWT token is valid, and authorization is successful. | Authentication: " + usernamePasswordAuthenticationToken);
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

		// continue with other chains
		chain.doFilter(request, response);
	}

	/**
	 * JWT token is valid, so create an UserDetails object with the given username.
	 * 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	private UserDetails createUserDetailsObject(String username) throws UsernameNotFoundException
	{
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		return new User(username, "", authorities);
	}

}
