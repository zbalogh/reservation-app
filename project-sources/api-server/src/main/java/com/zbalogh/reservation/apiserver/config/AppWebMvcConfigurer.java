package com.zbalogh.reservation.apiserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppWebMvcConfigurer implements WebMvcConfigurer {

	public AppWebMvcConfigurer() {
		super();
	}
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry)
	{
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
