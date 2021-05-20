package com.zbalogh.reservation.apiserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MyCustomBeanConfig {
	
	@Value(value = "${mycustombean.name}")
    private String mycustombeanName;
	
	@Value(value = "${mycustombean.title}")
    private String mycustombeanTitle;
	
	@Bean
	@Scope("singleton")
    public MyCustomBean myCustomBean()
	{
        return new MyCustomBean(mycustombeanName, mycustombeanTitle);
    }
	
	public static class MyCustomBean
	{
		private String name;
		private String title;
		
		public MyCustomBean(String name, String title)
		{
			this.name = name;
			this.title = title;
		}
		
		public String getName() {
			return name;
		}
		
		public String getTitle() {
			return title;
		}
	}

}
