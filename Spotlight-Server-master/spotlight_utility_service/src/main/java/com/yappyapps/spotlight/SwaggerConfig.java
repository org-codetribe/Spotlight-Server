package com.yappyapps.spotlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfig class enables each microservice to create the swagger docs.
 * 
 * <h1>@EnableSwagger2</h1> will enable the swagger documentation for REST
 * controllers.
 * 
 * <h1>@Configuration</h1> indicates that this class creates one or more Beans
 * which will be used by other classes.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(SwaggerConfig.class);

	/**
	 * This method is used to build the Docket bean with specifying swagger version,
	 * base package and provide in Spring context.
	 * 
	 * @return Docket: This returns the Docket bean.
	 */
	@Bean
	public Docket restApi() {
		LOGGER.debug("------------------- Swagger Docs Created ---------------------");
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.yappyapps.spotlight.controller"))
				.paths(PathSelectors.any()).build();
	}
}