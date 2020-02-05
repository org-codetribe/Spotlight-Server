package com.yappyapps.spotlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yappyapps.spotlight.util.Utils;

/**
 * The CouponServiceApplication class is the main entry point to start the spring boot
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
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-23
 */
@Configuration
@SpringBootApplication
@EnableDiscoveryClient
public class CouponServiceApplication {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(CouponServiceApplication.class);

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
	 * This method is used to create the Utils bean and provide in Spring context.
	 * 
	 * @return Utils: This returns the Utils bean.
	 */
	@Bean
	public Utils getUtils() {
		return new Utils();
	}

	/**
	 * This is the main method which will start the microservice.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		LOGGER.info("-------------------Spotlight Coupon Microservice Starting ---------------------");
		SpringApplication.run(CouponServiceApplication.class, args);
		LOGGER.info("-------------------Spotlight Coupon Microservice Started ---------------------");
	}
}
