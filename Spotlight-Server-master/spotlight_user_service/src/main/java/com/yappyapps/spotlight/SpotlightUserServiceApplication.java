package com.yappyapps.spotlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yappyapps.spotlight.util.Utils;

/**
 * The SpotlightUserServiceApplication class is the main entry point to start the spring boot
 * application.
 * 
 * <h1>@SpringBootApplication</h1> will start the microservice as Spring Boot
 * application.
 * 
 * <h1>@EnableDiscoveryClient</h1> will enable this microservice to register it
 * with Discovery Server.
 * 
 * <h1>@Configuration</h1> indicates that this class creates one or more Beans
 * which will be used by other classes.
 * 
 * <h1>@EnableAsync</h1> provides the asynchronous capability to the
 * application.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@EnableAsync
@Configuration
@SpringBootApplication
@EnableDiscoveryClient
public class SpotlightUserServiceApplication {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(SpotlightUserServiceApplication.class);

	/**
	 * This method is used to create the Gson bean and provide in Spring context.
	 * 
	 * @return Gson: This returns the Gson bean.
	 */
	@Bean
	public Gson gson() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
	}

	/**
	 * This method is used to create the Utils bean and provide in Spring context.
	 * 
	 * @return Utils: This returns the Utils bean.
	 */
	@Bean
	public Utils getUtils() {
		return new Utils();
	}

	/**
	 * This method is used to create the PasswordEncoder bean and provide in Spring
	 * context.
	 * 
	 * @return PasswordEncoder: This returns the PasswordEncoder bean.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * This is the main method which will start the microservice.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		LOGGER.info("-------------------Spotlight User Microservice Starting ---------------------");
		SpringApplication.run(SpotlightUserServiceApplication.class, args);
		LOGGER.info("-------------------Spotlight User Microservice Started ---------------------");
	}
}
