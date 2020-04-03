package com.yappyapps.spotlight.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.LiveStreamConfig;
import com.yappyapps.spotlight.domain.PricingRule;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IPricingRuleService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The PricingRuleController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on PricingRule.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "1.0/pricingrule")
public class PricingRuleController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PricingRuleController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "PricingRule";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * IPricingRuleService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IPricingRuleService pricingRuleService;

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
	 * This method is used to expose the REST API as POST to create PricingRule.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created PricingRule in JSON format.
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
	public @ResponseBody String createPricingRule(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createPricingRule";
		LOGGER.info("PricingRuleController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		PricingRule pricingRule = gson.fromJson(requestBody, PricingRule.class);

//		utils.isFloat(pricingRule.getDiscountPercentage(), "Discount Percentage");
		if( pricingRule.getDiscountPercentage() != null  ) {
			utils.isValidPercentage(pricingRule.getDiscountPercentage(), "Discount Percentage");
		}
		utils.isIntegerGreaterThanZero(pricingRule.getEventDuration(), "Event Duration");
		utils.isFloat(pricingRule.getMinimumPrice(), "Minimum Price");
//		utils.isValidDate(pricingRule.getDiscountValidTill(), "Discount Valid Till");
		utils.isStatusValid(pricingRule.getStatus());
		try {
			result = pricingRuleService.createPricingRule(pricingRule);
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
	 * This method is used to expose the REST API as GET to get all PricingRules
	 * with paging.
	 * 
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: PricingRules in JSON format.
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
	public @ResponseBody String getAllPricingRules(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllPricingRules";
		LOGGER.info("PricingRuleController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();

		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, PricingRule.class);
				result = pricingRuleService.getAllPricingRules(Integer.valueOf(limit), Integer.valueOf(offset),
						direction, orderBy);
			} else {
				result = pricingRuleService.getAllPricingRules();
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
	 * This method is used to expose the REST API as GET to get PricingRule by Id.
	 * 
	 * @param pricingRuleId:
	 *            String
	 * @return ResponseBody: PricingRule in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{pricingRuleId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getPricingRuleById(@PathVariable("pricingRuleId") String pricingRuleId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getPricingRuleById";
		LOGGER.info("PricingRuleController ::  " + operation + " :: pricingRuleId :: " + pricingRuleId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(pricingRuleId, "pricingRuleId");
		utils.isIntegerGreaterThanZero(pricingRuleId, "pricingRuleId");
		try {
			result = pricingRuleService.getPricingRule(Integer.parseInt(pricingRuleId));
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
	 * This method is used to expose the REST API as GET to get PricingRule by
	 * status.
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
	 * @return ResponseBody: PricingRule in JSON format
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
	public @ResponseBody String getPricingRuleByStatus(@PathVariable("status") String status,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getPricingRuleByStatus";
		LOGGER.info("PricingRuleController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(status, "Status");
		utils.isStatusValid(status);
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, LiveStreamConfig.class);
				result = pricingRuleService.getPricingRuleByStatus(status, Integer.valueOf(limit),
						Integer.valueOf(offset), direction, orderBy);
			} else {
				result = pricingRuleService.getPricingRuleByStatus(status);
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
	 * This method is used to expose the REST API as PUT to update PricingRule.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated PricingRule in JSON format.
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
	public @ResponseBody String updatePricingRule(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updatePricingRule";
		LOGGER.info("PricingRuleController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		PricingRule pricingRule = gson.fromJson(requestBody, PricingRule.class);
		utils.isEmptyOrNull(pricingRule.getId(), "Id");
		utils.isIntegerGreaterThanZero(pricingRule.getId(), "Id");
		if( pricingRule.getDiscountPercentage() != null  ) 
			utils.isValidPercentage(pricingRule.getDiscountPercentage(), "Discount Percentage");
		try {
			result = pricingRuleService.updatePricingRule(pricingRule);
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
	 * This method is used to expose the REST API as DELETE to delete PricingRule by
	 * Id.
	 * 
	 * @param pricingRuleId:
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
	@RequestMapping(value = "/id/{pricingRuleId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deletePricingRule(@PathVariable("pricingRuleId") String pricingRuleId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deletePricingRule";
		LOGGER.info("PricingRuleController :: " + operation + " :: pricingRuleId :: " + pricingRuleId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(pricingRuleId, "pricingRuleId");
		utils.isIntegerGreaterThanZero(pricingRuleId, "pricingRuleId");
		try {
			result = pricingRuleService.deletePricingRule(Integer.parseInt(pricingRuleId));
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
