package com.zbalogh.reservation.apiserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationAppConfig {
	
	@Value(value = "${reservation.alldesk_number}")
    private Integer alldeskNumber;
	
	@Value(value = "${reservation.jwt_secret_key}")
    private String jwtSecretKey;
	
	@Value(value = "${reservation.authServerName}")
	private String authServerName;


	public Integer getAlldeskNumber() {
		return alldeskNumber;
	}

	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

	public String getAuthServerName() {
		return authServerName;
	}

}
