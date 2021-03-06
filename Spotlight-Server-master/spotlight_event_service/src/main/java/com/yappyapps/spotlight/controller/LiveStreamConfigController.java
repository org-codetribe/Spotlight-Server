package com.yappyapps.spotlight.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.LiveStreamConfig;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.ILiveStreamConfigService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The LiveStreamConfigController class is the controller which will expose all
 * the required REST interfaces to perform CRUD on LiveStreamConfig.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "1.0/livestreamconfig")
public class LiveStreamConfigController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveStreamConfigController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "LiveStreamConfig";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * ILiveStreamConfigService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ILiveStreamConfigService liveStreamConfigService;

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
	 * This method is used to expose the REST API as POST to create
	 * LiveStreamConfig.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created LiveStreamConfig in JSON format.
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
	public @ResponseBody String createLiveStreamConfig(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createLiveStreamConfig";
		LOGGER.info("LiveStreamConfigController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		LiveStreamConfig liveStreamConfig = gson.fromJson(requestBody, LiveStreamConfig.class);

		utils.isEmptyOrNull(liveStreamConfig.getUsername(), "User Name");
		utils.isEmptyOrNull(liveStreamConfig.getPassword(), "Password");
		utils.isEmptyOrNull(liveStreamConfig.getHost(), "Host");
		utils.isEmptyOrNull(liveStreamConfig.getConnectionType(), "Connection Type");
		utils.isIntegerGreaterThanZero(liveStreamConfig.getPort(), "Port");
		try {
			result = liveStreamConfigService.createLiveStreamConfig(liveStreamConfig);
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
	 * This method is used to expose the REST API as GET to get all
	 * LiveStreamConfigs with paging.
	 * 
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: LiveStreamConfigs in JSON format.
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
	public @ResponseBody String getAllLiveStreamConfigs(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllLiveStreamConfigs";
		LOGGER.info("LiveStreamConfigController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, LiveStreamConfig.class);
				result = liveStreamConfigService.getAllLiveStreamConfigs(Integer.valueOf(limit),
						Integer.valueOf(offset), direction, orderBy);
			} else {
				result = liveStreamConfigService.getAllLiveStreamConfigs();
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
	 * This method is used to expose the REST API as GET to get LiveStreamConfig by
	 * Id.
	 * 
	 * @param liveStreamConfigId:
	 *            String
	 * @return ResponseBody: LiveStreamConfig in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{liveStreamConfigId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getLiveStreamConfigById(@PathVariable("liveStreamConfigId") String liveStreamConfigId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getLiveStreamConfigById";
		LOGGER.info("LiveStreamConfigController :: " + operation + " :: liveStreamConfigId :: " + liveStreamConfigId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(liveStreamConfigId, "liveStreamConfigId");
		utils.isIntegerGreaterThanZero(liveStreamConfigId, "liveStreamConfigId");
		try {
			result = liveStreamConfigService.getLiveStreamConfig(Integer.parseInt(liveStreamConfigId));
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
	 * This method is used to expose the REST API as PUT to update LiveStreamConfig.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated LiveStreamConfig in JSON format.
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
	public @ResponseBody String updateLiveStreamConfig(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateLiveStreamConfig";
		LOGGER.info("LiveStreamConfigController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		LiveStreamConfig liveStreamConfig = gson.fromJson(requestBody, LiveStreamConfig.class);
		utils.isEmptyOrNull(liveStreamConfig.getId(), "liveStreamConfigId");
		utils.isIntegerGreaterThanZero(liveStreamConfig.getId(), "liveStreamConfigId");
		try {
			result = liveStreamConfigService.updateLiveStreamConfig(liveStreamConfig);
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
	 * This method is used to expose the REST API as DELETE to delete
	 * LiveStreamConfig by Id.
	 * 
	 * @param liveStreamConfigId:
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
	@RequestMapping(value = "/id/{liveStreamConfigId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteLiveStreamConfig(@PathVariable("liveStreamConfigId") String liveStreamConfigId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteLiveStreamConfig";
		LOGGER.info("LiveStreamConfigController :: " + operation + " :: liveStreamConfigId :: " + liveStreamConfigId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(liveStreamConfigId, "liveStreamConfigId");
		utils.isIntegerGreaterThanZero(liveStreamConfigId, "liveStreamConfigId");
		try {
			result = liveStreamConfigService.deleteLiveStreamConfig(Integer.parseInt(liveStreamConfigId));
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
