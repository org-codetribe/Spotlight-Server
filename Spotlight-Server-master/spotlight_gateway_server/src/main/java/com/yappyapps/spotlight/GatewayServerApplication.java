package com.yappyapps.spotlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yappyapps.spotlight.filter.CORSFilter;
import com.yappyapps.spotlight.util.Utils;


/**
* The GatewayServerApplication class is the main entry point 
* to start the spring boot application.
* 
* <h1>@SpringBootApplication</h1> will start the 
* microservice as Spring Boot application.
* 
* <h1>@EnableDiscoveryClient</h1> will enable this 
* microservice to register it with Discovery Server.
* 
* <h1>@EnableZuulProxy</h1> will enable this 
* microservice to act as the API Gateway.
* 
* <h1>@Configuration</h1> indicates that this class
* creates one or more Beans which will be used by other 
* classes.
* 
* <h1>@EnableAsync</h1> provides the asynchronous capability 
* to the application. 
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
@SpringBootApplication
@EnableZuulProxy
@Configuration
@EnableDiscoveryClient
@EnableAsync
public class GatewayServerApplication {
	/**
	* Logger for the class.
	*/	
	private static Logger LOGGER = LoggerFactory.getLogger(GatewayServerApplication.class);
	
	/**
	* This method is used to register the CORS filter and providing FilterRegistrationBean in Spring context. 
	* @return FilterRegistrationBean: This returns the bean which has the CORS filter registered with order 1.
	*/	
	@Bean
	public FilterRegistrationBean corsFilter() { 
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOrigin("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");
	    source.registerCorsConfiguration("/**", config);
	    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
	    bean.setOrder(0);
	    return bean;
	}
	
	/**
	* This method is used to create the CORSFilter bean and provide in Spring context. 
	* @return CORSFilter: This returns the CORSFilter bean.
	*/	
	@Bean
	public CORSFilter corsesFilter()
	{
		return new CORSFilter();
	}

	/**
	* This method is used to create the Hibernate5Module bean and provide in Spring context. 
	* @return Module: This returns the Module bean.
	*/	
	@Bean
	public Module hibernate5Module()
	{
		return new Hibernate5Module();
	}
	
	/**
	* This method is used to create the Gson bean and provide in Spring context. 
	* @return Gson: This returns the Gson bean.
	*/	
	@Bean
	public Gson gson() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
	}

	/**
	* This method is used to create the Utils bean and provide in Spring context. 
	* @return Utils: This returns the Utils bean.
	*/	
	@Bean
	public Utils getUtils() {
		return new Utils();
	}

	/**
	* This method is used to create the PasswordEncoder bean and provide in Spring context. 
	* @return PasswordEncoder: This returns the PasswordEncoder bean.
	*/	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	/**
	* This is the main method which will start the microservice.
	* @param args String[]
	*/
	public static void main(String[] args) {
		LOGGER.info("-------------------Spotlight Gateway Server Microservice Starting ---------------------");
		new SpringApplicationBuilder(GatewayServerApplication.class).web(true).run(args);
		LOGGER.info("-------------------Spotlight Gateway Server Microservice Started ---------------------");
	}

}
