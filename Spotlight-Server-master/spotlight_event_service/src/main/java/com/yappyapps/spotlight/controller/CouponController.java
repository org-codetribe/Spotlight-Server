package com.yappyapps.spotlight.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.exception.AccessDeniedException;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IACLService;
import com.yappyapps.spotlight.service.ICouponService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The CouponController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on Coupon.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * <h1>@RequestMapping</h1> defines the base path to map the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "1.0/coupon")
public class CouponController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CouponController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "Coupon";

	/**
	 * ICouponService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ICouponService couponService;

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * IACLService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IACLService aclService;

	/**
	 * Gson dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Gson gson;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * This method is used to expose the REST API as POST to create Coupon.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @param token:
	 *            Authorization Token
	 * @return ResponseBody: Created Coupon in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String createCoupon(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType, @RequestHeader(value="Authorization", required=false) String token)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createCoupon";
		LOGGER.info("CouponController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType + " :: token :: " + token);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		Coupon coupon = gson.fromJson(requestBody, Coupon.class);
		
		utils.isEmptyOrNull(coupon.getEvent(), "Event");
		utils.isEmptyOrNull(coupon.getEvent().getId(), "Event Id");
		utils.isIntegerGreaterThanZero(coupon.getEvent().getId(), "Event Id");
		utils.isEmptyOrNull(coupon.getType(), "Coupon Type");
		utils.isCouponTypeValid(coupon.getType());
		utils.isStatusValid(coupon.getStatus());
		
		JSONObject reqJson = new JSONObject(requestBody);

		if(coupon.getType().equalsIgnoreCase("Multi")) {
			utils.isEmptyOrNull(coupon.getRedemptionLimit(), "Redemption Limit");
		} else {
			if(!reqJson.has("count"))
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter 'count' cannot be null or empty.");
			utils.isEmptyOrNull(reqJson.get("count"), "Coupon Count");
			utils.isIntegerGreaterThanZero(reqJson.get("count"), "Coupon Count");
			Integer count = Integer.parseInt(reqJson.get("count").toString());
			coupon.setCount(count);
		}

		try {
			if(IConstants.ACL_CHECK_ON && !aclService.isAllowed(token, coupon.getEvent())) {
				throw new AccessDeniedException(IConstants.ACCESS_DENIED_MESSAGE);
			}
			
			result = couponService.createCoupon(coupon);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (AlreadyExistException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(requestBody, token, controller, operation, (System.currentTimeMillis() - startTime),
					requestBody.length());
		}

		return result;
	}

//	/**
//	 * This method is used to expose the REST API as GET to get all Coupons with
//	 * paging.
//	 * 
//	 * @param limit:
//	 *            String
//	 * @param offset:
//	 *            String
//	 * @param direction:
//	 *            String
//	 * @param orderBy:
//	 *            String
//	 * @return ResponseBody: Coupons in JSON format.
//	 * 
//	 * @throws InvalidParameterException
//	 *             InvalidParameterException
//	 * @throws ResourceNotFoundException
//	 *             ResourceNotFoundException
//	 * @throws BusinessException
//	 *             BusinessException
//	 * 
//	 */
//	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
//	public @ResponseBody String getAllCoupons(@RequestParam(value = "limit", required = false) String limit,
//			@RequestParam(value = "offset", required = false) String offset,
//			@RequestParam(value = "direction", required = false) String direction,
//			@RequestParam(value = "orderBy", required = false) String orderBy)
//			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
//		String operation = "getAllCoupons";
//		LOGGER.info("CouponController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
//				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
//		long startTime = System.currentTimeMillis();
//		String result = null;
//		try {
//			if (offset != null && limit != null) {
//				utils.isInteger(offset, "offset");
//				utils.isInteger(limit, "limit");
//				utils.isOrderByDirectionValid(direction);
//				utils.isOrderByPropertyValid(orderBy, Coupon.class);
//
//				result = couponService.getAllCoupons(Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
//			} else {
//				result = couponService.getAllCoupons();
//			}
//		} catch (InvalidParameterException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (ResourceNotFoundException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (BusinessException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
//		} finally {
//			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
//		}
//		return result;
//	}
//
//	/**
//	 * This method is used to expose the REST API as GET to get Coupon by Id.
//	 * 
//	 * @param couponId:
//	 *            String
//	 * @return ResponseBody: Coupon in JSON format
//	 * 
//	 * @throws InvalidParameterException
//	 *             InvalidParameterException
//	 * @throws ResourceNotFoundException
//	 *             ResourceNotFoundException
//	 * @throws BusinessException
//	 *             BusinessException
//	 */
//	@RequestMapping(value = "/id/{couponId}", method = RequestMethod.GET, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public @ResponseBody String getCouponById(@PathVariable("couponId") String couponId)
//			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
//		String operation = "getCouponById";
//		LOGGER.info("CouponController :: " + operation + " :: couponId :: " + couponId);
//		long startTime = System.currentTimeMillis();
//
//		String result = "";
//		utils.isEmptyOrNull(couponId, "couponId");
//		utils.isIntegerGreaterThanZero(couponId, "couponId");
//		try {
//			result = couponService.getCoupon(Integer.parseInt(couponId));
//		} catch (InvalidParameterException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (ResourceNotFoundException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (BusinessException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
//		} finally {
//			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
//		}
//		return result;
//	}
//
//	/**
//	 * This method is used to expose the REST API as GET to get all Coupons by status.
//	 * 
//	 * @param status:
//	 *            String
//	 * @param limit:
//	 *            String
//	 * @param offset:
//	 *            String
//	 * @param direction:
//	 *            String
//	 * @param orderBy:
//	 *            String
//	 * @return ResponseBody: Coupons in JSON format.
//	 * 
//	 * @throws InvalidParameterException
//	 *             InvalidParameterException
//	 * @throws ResourceNotFoundException
//	 *             ResourceNotFoundException
//	 * @throws BusinessException
//	 *             BusinessException
//	 */
//	@RequestMapping(value = "/status/{status}", method = RequestMethod.GET, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public @ResponseBody String getCouponsByStatus(@PathVariable("status") String status,
//			@RequestParam(value = "limit", required = false) String limit,
//			@RequestParam(value = "offset", required = false) String offset,
//			@RequestParam(value = "direction", required = false) String direction,
//			@RequestParam(value = "orderBy", required = false) String orderBy)
//			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
//		String operation = "getCouponsByStatus";
//		LOGGER.info("CouponController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
//				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
//		long startTime = System.currentTimeMillis();
//		String result = null;
//		utils.isEmptyOrNull(status, "Status");
//		utils.isStatusValid(status);
//		try {
//			if (offset != null && limit != null) {
//				utils.isInteger(offset, "offset");
//				utils.isInteger(limit, "limit");
//				utils.isOrderByDirectionValid(direction);
//				utils.isOrderByPropertyValid(orderBy, Coupon.class);
//
//				result = couponService.getCouponsByStatus(status, Integer.valueOf(limit), Integer.valueOf(offset),
//						direction, orderBy);
//			} else {
//				result = couponService.getCouponsByStatus(status);
//			}
//		} catch (InvalidParameterException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (ResourceNotFoundException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (BusinessException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
//		} finally {
//			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
//		}
//		return result;
//	}
//
//	/**
//	 * This method is used to expose the REST API as PUT to update Coupon.
//	 * 
//	 * @param requestBody:
//	 *            Request Body in JSON Format.
//	 * @param contentType:
//	 *            "application/json"
//	 * @param token:
//	 *            Authorization Token
//	 * @return ResponseBody: Updated Coupon in JSON format.
//	 * 
//	 * @throws InvalidParameterException
//	 *             InvalidParameterException
//	 * @throws ResourceNotFoundException
//	 *             ResourceNotFoundException
//	 * @throws BusinessException
//	 *             BusinessException
//	 */
//	@RequestMapping(method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public @ResponseBody String updateCoupon(@RequestBody String requestBody,
//			@RequestHeader("Content-Type") String contentType, @RequestHeader(value="Authorization", required=false) String token)
//			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
//		String operation = "updateCoupon";
//		LOGGER.info("CouponController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
//				+ contentType + ":: token :: " + token);
//		long startTime = System.currentTimeMillis();
//		String result = "";
//		utils.isBodyJSONObject(requestBody);
//		Coupon coupon = gson.fromJson(requestBody, Coupon.class);
//		utils.isIntegerGreaterThanZero(coupon.getId(), "Id");
////		utils.isAvailableObjectEmpty(coupon.getName(), "Name");
//		try {
//			result = couponService.updateCoupon(coupon);
//		} catch (InvalidParameterException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (ResourceNotFoundException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (BusinessException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
//		} finally {
//			meteringService.record(requestBody, token, controller, operation, (System.currentTimeMillis() - startTime), 0);
//		}
//		return result;
//	}

	/**
	 * This method is used to expose the REST API as GET to get Coupon by EventId.
	 * 
	 * @param couponId:
	 *            String
	 * @return ResponseBody: Coupon in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/event/id/{eventId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getCouponsByEvent(@PathVariable("eventId") String eventId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getCouponsByEvent";
		LOGGER.info("CouponController :: " + operation + " :: eventId :: " + eventId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(eventId, "Event Id");
		utils.isIntegerGreaterThanZero(eventId, "Event Id");
		try {
			result = couponService.getCouponsByEvent(Integer.parseInt(eventId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as DELETE to delete Coupon by Id.
	 * 
	 * @param couponId:
	 *            String
	 * @return ResponseBody: Response in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{couponId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteCoupon(@PathVariable("couponId") String couponId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteCoupon";
		LOGGER.info("CouponController :: " + operation + " :: couponId :: " + couponId);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isEmptyOrNull(couponId, "couponId");
		utils.isIntegerGreaterThanZero(couponId, "couponId");
		try {
			result = couponService.deleteCoupon(Integer.parseInt(couponId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

}
