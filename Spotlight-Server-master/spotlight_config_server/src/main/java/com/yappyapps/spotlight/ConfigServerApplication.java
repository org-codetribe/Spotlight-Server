package com.yappyapps.spotlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
* The ConfigServerApplication class is the main entry point 
* to start the spring boot application.
* 
* <h1>@SpringBootApplication</h1> will start the 
* microservice as Spring Boot application.
* 
* <h1>@EnableDiscoveryClient</h1> will enable this 
* microservice to register it with Discovery Server.
* 
* <h1>@EnableConfigServer</h1> indicates that this 
* is the Cloud Config Server.
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {
	/**
	* Logger for the class.
	*/	
	private static Logger LOGGER = LoggerFactory.getLogger(ConfigServerApplication.class);
	
	
	/**
	* This is the main method which will start the microservice.
	* @param args String[]
	*/
	public static void main(String[] args) {
		LOGGER.info("-------------------Spotlight Config Server Microservice Starting ---------------------");
		SpringApplication.run(ConfigServerApplication.class, args);
		LOGGER.info("-------------------Spotlight Config Server Microservice Started ---------------------");
	}
}
