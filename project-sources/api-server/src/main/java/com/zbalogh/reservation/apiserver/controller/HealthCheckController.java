package com.zbalogh.reservation.apiserver.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zbalogh.reservation.apiserver.utils.AppConstants;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class HealthCheckController {
	
	@RequestMapping(AppConstants.RESTAPI_BASE_URL + "/health")
	public String apiHealthCheck()
	{
		return "{healthy:true}";
	}
	
	@GetMapping("/")
	public String rootHealthCheck()
	{
		return "{healthy:true}";
	}

}
