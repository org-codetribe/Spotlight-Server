package com.yappyapps.spotlight.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The AdminUserController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on Spotlight(Admin)User.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/admin")
public class AdminUserController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "AdminUser";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * ISpotlightUserService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserService spotlightUserService;

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
	 * Spotlight(Admin)User.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created Spotlight(Admin)User in JSON format.
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
	public @ResponseBody String createAdminUser(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createAdminUser";
		LOGGER.debug("AdminUserController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);

		utils.isEmptyOrNull(spotlightUser.getEmail(), "Email");
		utils.isEmailValid(spotlightUser.getEmail());
		utils.isEmptyOrNull(spotlightUser.getName(), "Name");
		utils.isEmptyOrNull(spotlightUser.getUserType(), "User Type");
		utils.isUserTypeValid(spotlightUser.getUserType());
		utils.isStatusValid(spotlightUser.getStatus());
		try {
			result = spotlightUserService.createSpotlightUser(spotlightUser);
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
	 * Spotlight(Admin)Users with paging.
	 * 
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Spotlight(Admin)Users in JSON format.
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
	public @ResponseBody String getAllAdminUsers(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllAdminUsers";
		LOGGER.info("AdminUserController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;

		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, SpotlightUser.class);
				result = spotlightUserService.getSpotlightUsersByType("ADMIN", Integer.valueOf(limit),
						Integer.valueOf(offset), direction, orderBy);
			} else {
				result = spotlightUserService.getSpotlightUsersByType("ADMIN");
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
	 * This method is used to expose the REST API as GET to get Spotlight(Admin)User
	 * by Id.
	 * 
	 * @param userId:
	 *            String
	 * @return ResponseBody: Spotlight(Admin)User in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{userId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAdminUserById(@PathVariable("userId") String userId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAdminUserById";
		LOGGER.info("AdminUserController :: " + operation + " :: userId :: " + userId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(userId, "userId");
		utils.isIntegerGreaterThanZero(userId, "userId");
		try {
			result = spotlightUserService.getSpotlightUser(Integer.parseInt(userId));
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
	 * This method is used to expose the REST API as PUT to update
	 * Spotlight(Admin)User.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated Spotlight(Admin)User in JSON format.
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
	public @ResponseBody String updateAdminUser(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateAdminUser";
		LOGGER.info("AdminUserController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		String result = "";
		long startTime = System.currentTimeMillis();
		utils.isBodyJSONObject(requestBody);
		SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
		utils.isEmptyOrNull(spotlightUser.getId(), "id");
		utils.isIntegerGreaterThanZero(spotlightUser.getId(), "id");
		try {
			result = spotlightUserService.updateSpotlightUser(spotlightUser);
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
	 * This method is used to expose the REST API as PUT to update
	 * Spotlight(Admin)User status.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated Spotlight(Admin)User in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/status", method = RequestMethod.PUT, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String updateAdminUserStatus(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateAdminUserStatus";
		LOGGER.info("AdminUserController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		String result = "";
		long startTime = System.currentTimeMillis();
		utils.isBodyJSONObject(requestBody);
		SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
		utils.isEmptyOrNull(spotlightUser.getId(), "id");
		utils.isIntegerGreaterThanZero(spotlightUser.getId(), "id");
		try {
			result = spotlightUserService.updateSpotlightUser(spotlightUser);
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
	 * This method is used to expose the REST API as PUT to update
	 * Spotlight(Admin)User role.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated Spotlight(Admin)User in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/role", method = RequestMethod.PUT, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String updateAdminUserRole(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateAdminUserRole";
		LOGGER.info("AdminUserController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		String result = "";
		long startTime = System.currentTimeMillis();
		utils.isBodyJSONObject(requestBody);
		SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
		utils.isEmptyOrNull(spotlightUser.getId(), "id");
		utils.isIntegerGreaterThanZero(spotlightUser.getId(), "id");
		try {
			result = spotlightUserService.updateSpotlightUserRole(spotlightUser);
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
	 * Spotlight(Admin)User by Id.
	 * 
	 * @param userId:
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
	@RequestMapping(value = "/id/{userId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteAdminUser(@PathVariable("userId") String userId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteAdminUser";
		LOGGER.info("AdminUserController :: " + operation + " :: userId :: " + userId);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isEmptyOrNull(userId, "UserId");
		utils.isIntegerGreaterThanZero(userId, "userId");
		try {
			result = spotlightUserService.deleteSpotlightUser(Integer.parseInt(userId));
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
