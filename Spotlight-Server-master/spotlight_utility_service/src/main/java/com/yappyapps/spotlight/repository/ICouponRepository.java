package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.domain.Event;

/**
 * The ICouponRepository interface provides the CRUD operations on Coupon domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface ICouponRepository extends CrudRepository<Coupon, Integer> {

	/**
	 * This method is used to find all Coupons with paging and orderBy.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return List&lt;Page&gt;
	 */
	Page<Coupon> findAll(Pageable pageable);

	/**
	 * This method is used to find all Coupons orderBy number asc.
	 * 
	 * @return List&lt;Coupon&gt;
	 */
	List<Coupon> findAllByOrderByNumber();

	/**
	 * This method is used to find all Coupons by status orderBy number asc.
	 * 
	 * @param status:
	 *            String
	 * @return List&lt;Coupon&gt;
	 */
	List<Coupon> findAllByStatusOrderByNumber(String status);

	/**
	 * This method is used to find all Coupons by status with paging and orderBy.
	 * 
	 * @param status:
	 *            String
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;Coupon&gt;
	 */
	Page<Coupon> findAllByStatus(String status, Pageable pageable);

	/**
	 * This method is used to find all Coupons by Event.
	 * 
	 * @param event:
	 *            Event
	 * @return List&lt;Coupon&gt;
	 */
	List<Coupon> findByEvent(Event event);

	/**
	 * This method is used to find the Coupon by Number.
	 * 
	 * @param number:
	 *            String
	 * @return Coupon
	 */
	Coupon findByNumber(String number);

}
