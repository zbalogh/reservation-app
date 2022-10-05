package com.zbalogh.reservation.apiserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.zbalogh.reservation.apiserver.config.MyCustomBeanConfig.MyCustomBean;
import com.zbalogh.reservation.apiserver.grpc.services.UserGrpcService;
import com.zbalogh.reservation.apiserver.services.DatabaseInitializer;

/**
@SpringBootApplication is a convenience annotation that adds all of the following:

@Configuration tags the class as a source of bean definitions for the application context.

@EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.

@ComponentScan tells Spring to look for other components, configurations, and services in the hello package, allowing it to find the controllers.
*/
//
// https://www.yawintutor.com/using-generated-security-password-spring-boot/
//
// https://stackoverflow.com/questions/30761253/remove-using-default-security-password-on-spring-boot
//
//@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class})
@EnableJpaRepositories
public class ReservationApiServerApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ReservationApiServerApplication.class);
	
	@Autowired
	private DatabaseInitializer databaseInitializer;
	
	// only for testing the @Bean annotation in the MyCustomBeanConfig class
	@Autowired
	private MyCustomBean myCustomBean;
	
	@Autowired
	private UserGrpcService userGrpcService;
	
	
	/**
	 * The main method runs the Spring Boot Application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		logger.info("Starting up Spring Boot application...");
		ConfigurableApplicationContext context = SpringApplication.run(ReservationApiServerApplication.class, args);
		logger.info("Spring Boot application running.");
		
		// only for testing the @Bean annotation in the MyCustomBeanConfig class
		MyCustomBean bean = context.getBean(MyCustomBean.class);
		logger.info("bean: " + bean + " | name=" + bean.getName() + ", title=" + bean.getTitle());
	}

	/**
	 * Execute some code after the application started up.
	 */
	@Override
	public void run(String... args) throws Exception
	{
		logger.info("Creating the initial data in the database...");
		databaseInitializer.createInitialData();
		logger.info("The initial data has been created.");
		
		// only for testing the @Bean annotation in the MyCustomBeanConfig class
		logger.info("myCustomBean: " + myCustomBean + " | name=" + myCustomBean.getName() + ", title=" + myCustomBean.getTitle());
		
		// initialize UserGrpcClient with host and port
		try {
			userGrpcService.init();
		}
		catch (Exception ex) {
			logger.error("Exception while initializing UserGrpcClient.", ex);
		}
	}

}
