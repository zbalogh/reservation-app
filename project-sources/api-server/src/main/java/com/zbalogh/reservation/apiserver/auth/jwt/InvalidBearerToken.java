package com.zbalogh.reservation.apiserver.auth.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * This is a customized authentication exception which occurs when the provided bearer token (JWT) is invalid.
 */
public class InvalidBearerToken extends AuthenticationException
{
	private static final long serialVersionUID = -7761614892397564429L;
	
	public InvalidBearerToken(String msg) {
		super(msg);
	}

	public InvalidBearerToken(String msg, Throwable cause) {
		super(msg, cause);
	}
}
