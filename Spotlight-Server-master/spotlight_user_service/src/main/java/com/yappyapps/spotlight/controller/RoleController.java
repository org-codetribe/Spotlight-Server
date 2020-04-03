package com.yappyapps.spotlight.controller;

import com.yappyapps.spotlight.domain.Role;
import com.yappyapps.spotlight.service.IRoleService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
 * The SpotlightUserController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on SpotlightUser.
 *
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/role/")

@CrossOrigin(value = "*")
public class RoleController {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    /**
     * Controller Name.
     */
    private static final String controller = "RoleController";

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
    private IRoleService roleService;

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
     * This method is used to expose the REST API as POST to create SpotlightUser.
     *
     * @param requestBody:
     *            Request Body in JSON Format.
     * @param contentType:
     *            "application/json"
     * @return ResponseBody: Created SpotlightUser in JSON format.
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
    public @ResponseBody String createRole(@RequestBody String requestBody,
                                                    @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "createRole";
        LOGGER.debug("RoleController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        Role role = gson.fromJson(requestBody, Role.class);


        utils.isEmptyOrNull(role.getName(), "Name");
        utils.isStatusValid(role.getStatus());
        try {
            result = roleService.createRole(role);
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
     * This method is used to expose the REST API as GET to get all SpotlightUsers
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
     * @return ResponseBody: SpotlightUsers in JSON format.
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
    public @ResponseBody String getAllRoles(@RequestParam(value = "limit", required = false) String limit,
                                                     @RequestParam(value = "offset", required = false) String offset,
                                                     @RequestParam(value = "direction", required = false) String direction,
                                                     @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAllSpotlightUsers";
        LOGGER.info("SpotlightUserController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = null;

        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, SpotlightUser.class);
                result = roleService.getAllRoles(Integer.valueOf(limit), Integer.valueOf(offset),
                        direction, orderBy);
            } else {
                result = roleService.getAllRoles();
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
     * This method is used to expose the REST API as GET to get SpotlightUser by Id.
     *
     * @param userId:
     *            String
     * @return ResponseBody: SpotlightUser in JSON format
     *
     * @throws InvalidParameterException
     *             InvalidParameterException
     * @throws ResourceNotFoundException
     *             ResourceNotFoundException
     * @throws BusinessException
     *             BusinessException
     */
    @RequestMapping(value = "/id/{userId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody String getSpotlightUserById(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getSpotlightUserById";
        LOGGER.info("SpotlightUserController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(userId, "userId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = roleService.getRoleById(Integer.parseInt(userId));
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
     * This method is used to expose the REST API as GET to get SpotlightUser by Id.
     *
     * @param userId:
     *            String
     * @return ResponseBody: SpotlightUser in JSON format
     *
     * @throws InvalidParameterException
     *             InvalidParameterException
     * @throws ResourceNotFoundException
     *             ResourceNotFoundException
     * @throws BusinessException
     *             BusinessException
     */
    @RequestMapping(value = "/profile/id/{userId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody String getSpotlightUserProfileById(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getSpotlightUserById";
        LOGGER.info("SpotlightUserController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(userId, "userId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = roleService.getRoleById(Integer.parseInt(userId));
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
     * This method is used to expose the REST API as GET to get SpotlightUsers by
     * type by Id.
     *
     * @param userType:
     *            String
     * @param limit:
     *            String
     * @param offset:
     *            String
     * @param direction:
     *            String
     * @param orderBy:
     *            String
     * @return ResponseBody: SpotlightUsers in JSON format
     *
     * @throws InvalidParameterException
     *             InvalidParameterException
     * @throws ResourceNotFoundException
     *             ResourceNotFoundException
     * @throws BusinessException
     *             BusinessException
     */

    /**
     * This method is used to expose the REST API as PUT to change SpotlightUser
     * password.
     *
     * @param requestBody:
     *            Request Body in JSON Format.
     * @param contentType:
     *            "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
     *
     * @throws InvalidParameterException
     *             InvalidParameterException
     * @throws ResourceNotFoundException
     *             ResourceNotFoundException
     * @throws BusinessException
     *             BusinessException
     */


    /**
     * This method is used to expose the REST API as PUT to update SpotlightUser.
     *
     * @param requestBody:
     *            Request Body in JSON Format.
     * @param contentType:
     *            "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
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
    public @ResponseBody String updateRole(@RequestBody String requestBody,
                                                    @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateSpotlightUser";
        LOGGER.info("SpotlightUserController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Role role = gson.fromJson(requestBody, Role.class);
        utils.isEmptyOrNull(role.getId(), "id");
        utils.isIntegerGreaterThanZero(role.getId(), "id");
        // utils.isEmptyOrNull(spotlightUser.getEmail(), "Email");
        try {
            result = roleService.updateRole(role);
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
     * This method is used to expose the REST API as PUT to update SpotlightUser
     * status.
     *
     * @param requestBody:
     *            Request Body in JSON Format.
     * @param contentType:
     *            "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
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
    public @ResponseBody String updateRoleStatus(@RequestBody String requestBody,
                                                          @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateRoleStatus";
        LOGGER.info("RoleController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Role role = gson.fromJson(requestBody, Role.class);
        utils.isEmptyOrNull(role.getId(), "id");
        utils.isIntegerGreaterThanZero(role.getId(), "id");
        try {
            result = roleService.updateRole(role);
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
