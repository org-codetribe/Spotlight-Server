package com.yappyapps.spotlight.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
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
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
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
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createCoupon";
		LOGGER.info("CouponController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		Coupon coupon = gson.fromJson(requestBody, Coupon.class);

		utils.isEmptyOrNull(coupon.getRedemptionLimit(), "RedemptionLimit");
		utils.isStatusValid(coupon.getStatus());
		try {
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
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
					requestBody.length());
		}

		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get all Coupons with
	 * paging.
	 * 
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Coupons in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAllCoupons(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllCoupons";
		LOGGER.info("CouponController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, Coupon.class);

				result = couponService.getAllCoupons(Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
			} else {
				result = couponService.getAllCoupons();
			}
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
	 * This method is used to expose the REST API as GET to get Coupon by Id.
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
	@RequestMapping(value = "/id/{couponId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getCouponById(@PathVariable("couponId") String couponId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getCouponById";
		LOGGER.info("CouponController :: " + operation + " :: couponId :: " + couponId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(couponId, "couponId");
		utils.isIntegerGreaterThanZero(couponId, "couponId");
		try {
			result = couponService.getCoupon(Integer.parseInt(couponId));
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
	 * This method is used to expose the REST API as GET to get all Coupons by status.
	 * 
	 * @param status:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Coupons in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/status/{status}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getCouponsByStatus(@PathVariable("status") String status,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getCouponsByStatus";
		LOGGER.info("CouponController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		utils.isEmptyOrNull(status, "Status");
		utils.isStatusValid(status);
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, Coupon.class);

				result = couponService.getCouponsByStatus(status, Integer.valueOf(limit), Integer.valueOf(offset),
						direction, orderBy);
			} else {
				result = couponService.getCouponsByStatus(status);
			}
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
	 * This method is used to expose the REST API as PUT to update Coupon.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated Coupon in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String updateCoupon(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateCoupon";
		LOGGER.info("CouponController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		Coupon coupon = gson.fromJson(requestBody, Coupon.class);
		utils.isIntegerGreaterThanZero(coupon.getId(), "Id");
//		utils.isAvailableObjectEmpty(coupon.getName(), "Name");
		try {
			result = couponService.updateCoupon(coupon);
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

	/**
	 * This method is used to catch ResourceNotFoundException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException IOException
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	void handleNotFoundExceptions(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());
	}

	/**
	 * This method is used to catch InvalidParameterException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException IOException
	 */
	@ExceptionHandler(InvalidParameterException.class)
	void handleBadRequests(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * This method is used to catch AlreadyExistException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException IOException
	 */
	@ExceptionHandler(AlreadyExistException.class)
	void handleAlreadyExistRequests(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.CONFLICT.value());
	}

	/**
	 * This method is used to catch BusinessException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException IOException
	 */
	@ExceptionHandler(BusinessException.class)
	void handleInternalServerErrors(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}
