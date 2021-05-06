package com.zbalogh.reservation.apiserver.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zbalogh.reservation.apiserver.utils.AppConstants;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class HelloController {

	@RequestMapping(AppConstants.RESTAPI_BASE_URL + "/hello")
	public String hello()
	{
		return "Greetings from Spring Boot! [appId=" + AppConstants.APP_INSTANCE_UUID + "]";
	}

}
