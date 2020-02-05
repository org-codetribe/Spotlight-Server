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
import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IAccessRoleService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The AccessRoleController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on AccessRole.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/role")
public class AccessRoleController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessRoleController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "AccessRole";

	/**
	 * IAccessRoleService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IAccessRoleService accessRoleService;

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
	 * This method is used to expose the REST API as POST to create AccessRole.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created AccessRole in JSON format.
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
	public @ResponseBody String createAccessRole(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createAccessRole";
		LOGGER.debug("AccessRoleController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		AccessRole accessRole = gson.fromJson(requestBody, AccessRole.class);

		utils.isEmptyOrNull(accessRole.getName(), "Name");
		utils.isEmptyOrNull(accessRole.getType(), "Type");
		utils.isAccessRoleTypeValid(accessRole.getType());
		utils.isEmptyOrNull(accessRole.getPermissions(), "Permissions");
		utils.isJSONArray(accessRole.getPermissions(), "Permissions");
		try {
			result = accessRoleService.createAccessRole(accessRole);
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
	 * This method is used to expose the REST API as GET to get all AccessRoles with
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
	 * @return ResponseBody: AccessRoles in JSON format.
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
	public @ResponseBody String getAllAccessRoles(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllAccessRoles";
		LOGGER.info("AccessRoleController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, AccessRole.class);
				result = accessRoleService.getAllAccessRoles(Integer.valueOf(limit), Integer.valueOf(offset), direction,
						orderBy);
			} else {
				result = accessRoleService.getAllAccessRoles();
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
	 * This method is used to expose the REST API as GET to get AccessRole by Id.
	 * 
	 * @param roleId:
	 *            String
	 * @return ResponseBody: AccessRole in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{roleId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAccessRoleById(@PathVariable("roleId") String roleId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAccessRoleById";
		LOGGER.info("AccessRoleController :: " + operation + " :: roleId :: " + roleId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(roleId, "roleId");
		utils.isIntegerGreaterThanZero(roleId, "roleId");
		try {
			result = accessRoleService.getAccessRoleById(Integer.valueOf(roleId));
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
	 * This method is used to expose the REST API as PUT to update AccessRole.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated AccessRole in JSON format.
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
	public @ResponseBody String updateAccessRole(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateAccessRole";
		LOGGER.info("AccessRoleController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		AccessRole accessRole = gson.fromJson(requestBody, AccessRole.class);
		utils.isEmptyOrNull(accessRole.getId(), "id");
		utils.isIntegerGreaterThanZero(accessRole.getId(), "id");
		utils.isJSONArray(accessRole.getPermissions(), "permissions");
		utils.isAccessRoleTypeValid(accessRole.getType());

		try {
			result = accessRoleService.updateAccessRole(accessRole);
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
	 * This method is used to expose the REST API as DELETE to delete AccessRole by
	 * Id.
	 * 
	 * @param roleId:
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
	@RequestMapping(value = "/id/{roleId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteAccessRole(@PathVariable("roleId") String roleId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteAccessRole";
		LOGGER.info("AccessRoleController :: " + operation + " :: roleId :: " + roleId);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isEmptyOrNull(roleId, "roleId");
		utils.isIntegerGreaterThanZero(roleId, "roleId");

		try {
			result = accessRoleService.deleteAccessRole(Integer.valueOf(roleId));
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
