package com.yappyapps.spotlight.service.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration 
@Configuration
public class HibernateSearchConfiguration {

	@Autowired
	private EntityManager bentityManager;

	@Bean
	SearchService searchService() {
		SearchService searchService = new SearchService(bentityManager);
		searchService.initializeHibernateSearch();
		return searchService;
	}
}