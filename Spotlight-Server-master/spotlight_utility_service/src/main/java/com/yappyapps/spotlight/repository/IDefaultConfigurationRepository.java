package com.yappyapps.spotlight.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.DefaultConfiguration;

/**
 * The IDefaultConfigurationRepository interface provides the CRUD operations on
 * DefaultConfiguration domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IDefaultConfigurationRepository extends CrudRepository<DefaultConfiguration, Integer> {

	/**
	 * This method is used to find all DefaultConfigurations by paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;DefaultConfiguration&gt;
	 */
	Page<DefaultConfiguration> findAll(Pageable pageable);

	/**
	 * This method is used to find DefaultConfiguration by name.
	 * 
	 * @param name:
	 *            String
	 * @return DefaultConfiguration;
	 */
	DefaultConfiguration findByName(String name);
}
