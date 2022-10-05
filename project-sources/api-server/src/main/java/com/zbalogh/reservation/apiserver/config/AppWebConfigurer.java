package com.zbalogh.reservation.apiserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class AppWebConfigurer implements WebFluxConfigurer {

	public AppWebConfigurer() {
		super();
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry)
	{
		// enable CORS
        registry.addMapping("/**")
        	.allowedOrigins("*")
        	.allowedMethods("*")
        	.allowedHeaders("*");
    }

}
