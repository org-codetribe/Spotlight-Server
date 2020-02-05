package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.domain.CouponConsumption;
import com.yappyapps.spotlight.domain.Event;

/**
 * The ICouponConsumptionRepository interface provides the CRUD operations on CouponConsumption domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface ICouponConsumptionRepository extends CrudRepository<CouponConsumption, Integer> {

	List<CouponConsumption> findByCoupon(Coupon coupon);

	Long countByEvent(Event event);

	Long countByCoupon(Coupon coupon);


}
