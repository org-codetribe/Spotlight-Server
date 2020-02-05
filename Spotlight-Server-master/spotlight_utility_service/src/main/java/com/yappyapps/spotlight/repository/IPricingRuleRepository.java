package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.PricingRule;

/**
 * The IPricingRuleRepository interface provides the CRUD operations on
 * PricingRule domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Repository
public interface IPricingRuleRepository extends CrudRepository<PricingRule, Integer> {

	/**
	 * This method is used to find the PricingRule based on status.
	 * 
	 * @param status:
	 *            String
	 * @return List&lt;PricingRule&gt;
	 */
	List<PricingRule> findAllByStatus(String status);

	/**
	 * This method is used to find the PricingRule based on status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;PricingRule&gt;
	 */
	Page<PricingRule> findAllByStatus(String status, Pageable pageable);

	/**
	 * This method is used to find the PricingRule with paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;PricingRule&gt;
	 */
	Page<PricingRule> findAll(Pageable pageable);

}
