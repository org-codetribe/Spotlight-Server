package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The ICouponService interface declares all the operations to act upon Coupon
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public interface ICouponService {
	/**
	 * This method is used to create the Coupon
	 * 
	 * @param couponReqObj:
	 *            Coupon
	 * @return String: Response
	 * 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 * 
	 */
	public String createCoupon(Coupon couponReqObj) throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all Coupon.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllCoupons() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Coupons with paging.
	 * 
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllCoupons(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get Coupon by couponId.
	 * 
	 * @param couponId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getCoupon(Integer couponId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Coupons by Status.
	 * 
	 * @param status:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getCouponsByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Coupons by Status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getCouponsByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the Coupon.
	 * 
	 * @param couponReqObj:
	 *            Coupon
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String updateCoupon(Coupon couponReqObj) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to delete the Coupon by id.
	 * 
	 * @param couponId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String deleteCoupon(Integer couponId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;
}
