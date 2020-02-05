package com.yappyapps.spotlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
/**
* The DiscoveryServerApplication class is the main entry point 
* to start the spring boot application.
* 
* <h1>@SpringBootApplication</h1> will start the 
* microservice as Spring Boot application.
* 
* <h1>@EnableDiscoveryServer</h1> will enable it 
* to act as Discovery Server.
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServerApplication {
	/**
	* Logger for the class.
	*/	
	private static Logger LOGGER = LoggerFactory.getLogger(DiscoveryServerApplication.class);

	/**
	* This is the main method which will start the microservice.
	* @param args String[]
	*/
	public static void main(String[] args) {
		LOGGER.info("-------------------Spotlight Discovery Server Starting ---------------------");
		SpringApplication.run(DiscoveryServerApplication.class, args);
		LOGGER.info("-------------------Spotlight Discovery Server Started ---------------------");
    }
}
