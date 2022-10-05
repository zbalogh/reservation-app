package com.zbalogh.reservation.apiserver.auth.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Simple wrapper (holder) object for Bearer authentication token (e.g. JWT).
 * 
 * @author ZBalogh
 *
 */
public class BearerAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -7203046263161725456L;
	
	private final String token;

	public BearerAuthenticationToken(String token) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.token = token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	public String getToken() {
		return token;
	}

}
