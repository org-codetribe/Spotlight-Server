package com.yappyapps.spotlight.controller;

import org.json.JSONObject;
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
import com.yappyapps.spotlight.domain.Favorite;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IViewerService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

import java.util.Date;

/**
 * The ViewerController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on Viewer.
 *
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/viewer")
public class ViewerController {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerController.class);

    /**
     * Controller Name.
     */
    private static final String controller = "Viewer";

    /**
     * MeteringService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private MeteringService meteringService;

    /**
     * IViewerService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerService viewerService;

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
     * This method is used to expose the REST API as POST to create Viewer.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Created Viewer in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String createViewer(@RequestBody String requestBody,
                        @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "createViewer";
        LOGGER.debug("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        Viewer viewer = gson.fromJson(requestBody, Viewer.class);

        utils.isEmptyOrNull(viewer.getEmail(), "Email");
        utils.isEmailValid(viewer.getEmail());
        utils.isEmptyOrNull(viewer.getFname(), "Name");
        utils.isEmptyOrNull(viewer.getPassword(), "Password");
        //utils.isEmptyOrNull(viewer.getChatName(), "Chat Name");
        viewer.setChatName(new Date().getTime() + "".trim());
        utils.isStatusValid(viewer.getStatus());
        try {
            result = viewerService.createViewer(viewer);
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
     * This method is used to expose the REST API as POST to mark Broadcaster as Favorite.
     *
     * @param requestBody:  Request Body in JSON Format.
     * @param favoriteFlag: String
     * @param contentType:  "application/json"
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/favorite/broadcaster/{favoriteFlag}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String manageFavoriteBroadcaster(@RequestBody String requestBody, @PathVariable("favoriteFlag") String favoriteFlag,
                                     @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "manageFavoriteBroadcaster";
        LOGGER.debug("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: favoriteFlag :: " + favoriteFlag + " :: contentType :: "
                + contentType);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        Favorite favorite = gson.fromJson(requestBody, Favorite.class);

        utils.isEmptyOrNull(favorite.getBroadcasterInfo(), "Broadcaster");
        utils.isEmptyOrNull(favorite.getBroadcasterInfo().getId(), "Broadcaster Id");
        utils.isIntegerGreaterThanZero(favorite.getBroadcasterInfo().getId(), "Broadcaster Id");
        utils.isEmptyOrNull(favorite.getViewer(), "Viewer");
        utils.isEmptyOrNull(favorite.getViewer().getId(), "Viewer Id");
        utils.isIntegerGreaterThanZero(favorite.getViewer().getId(), "Viewer Id");
        utils.isEmptyOrNull(favoriteFlag, "favoriteFlag");

        try {
            result = viewerService.manageFavoriteBroadcaster(favorite, Boolean.valueOf(favoriteFlag));
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
     * This method is used to expose the REST API as POST to mark Event as Favorite.
     *
     * @param requestBody:  Request Body in JSON Format.
     * @param favoriteFlag: String
     * @param contentType:  "application/json"
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/favorite/event/{favoriteFlag}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String manageFavoriteEvent(@RequestBody String requestBody, @PathVariable("favoriteFlag") String favoriteFlag,
                               @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "manageFavoriteEvent";
        LOGGER.debug("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: favoriteFlag :: " + favoriteFlag + " :: contentType :: "
                + contentType);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        Favorite favorite = gson.fromJson(requestBody, Favorite.class);

        utils.isEmptyOrNull(favorite.getEvent(), "Event");
        utils.isEmptyOrNull(favorite.getEvent().getId(), "Event Id");
        utils.isIntegerGreaterThanZero(favorite.getEvent().getId(), "Event Id");
        utils.isEmptyOrNull(favorite.getViewer(), "Viewer");
        utils.isEmptyOrNull(favorite.getViewer().getId(), "Viewer Id");
        utils.isIntegerGreaterThanZero(favorite.getViewer().getId(), "Viewer Id");
        utils.isEmptyOrNull(favoriteFlag, "favoriteFlag");
        try {
            result = viewerService.manageFavoriteEvent(favorite, Boolean.valueOf(favoriteFlag));
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
     * This method is used to expose the REST API as GET to get all Viewers with
     * paging.
     *
     * @param limit:     String
     * @param offset:    String
     * @param direction: String
     * @param orderBy:   String
     * @return ResponseBody: Viewers in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getAllViewers(@RequestParam(value = "limit", required = false) String limit,
                         @RequestParam(value = "offset", required = false) String offset,
                         @RequestParam(value = "direction", required = false) String direction,
                         @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAllViewers";
        LOGGER.info("ViewerController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = null;

        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Viewer.class);
                result = viewerService.getAllViewers(Integer.valueOf(limit), Integer.valueOf(offset), direction,
                        orderBy);
            } else {
                result = viewerService.getAllViewers();
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
     * This method is used to expose the REST API as GET to get Viewer by Id.
     *
     * @param userId: String
     * @return ResponseBody: Viewer in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{userId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getViewerById(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getViewerById";
        LOGGER.info("ViewerController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(userId, "userId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = viewerService.getViewer(Integer.parseInt(userId));
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
     * This method is used to expose the REST API as GET to get Viewer by Id.
     *
     * @param userId: String
     * @return ResponseBody: Viewer in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/profile/id/{userId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getViewerProfileById(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getViewerById";
        LOGGER.info("ViewerController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(userId, "userId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = viewerService.getViewer(Integer.parseInt(userId));
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
     * This method is used to expose the REST API as GET to get Viewer's Favorite Broadcaster.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param viewerId:    String
     * @param contentType: "application/json"
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/{viewerId}/favorite/broadcaster", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getFavoriteBroadcaster(@PathVariable("viewerId") String viewerId,
                                  @RequestParam(value = "limit", required = false) String limit,
                                  @RequestParam(value = "offset", required = false) String offset,
                                  @RequestParam(value = "direction", required = false) String direction,
                                  @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getFavoriteBroadcasters";
        LOGGER.debug("ViewerController :: " + operation + " :: viewerId :: " + viewerId + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = "";

        utils.isEmptyOrNull(viewerId, "viewerId");
        utils.isIntegerGreaterThanZero(viewerId, "viewerId");
        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Viewer.class);
                result = viewerService.getFavoriteBroadcasters(Integer.valueOf(viewerId), Integer.valueOf(limit), Integer.valueOf(offset), direction,
                        orderBy);
            } else {
                result = viewerService.getFavoriteBroadcasters(Integer.valueOf(viewerId));
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
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
                    0);
        }

        return result;
    }

    /**
     * This method is used to expose the REST API as GET to get Viewer's Favorite Events.
     *
     * @param requestBody:  Request Body in JSON Format.
     * @param favoriteFlag: String
     * @param contentType:  "application/json"
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/{viewerId}/favorite/event", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getFavoriteEvents(@PathVariable("viewerId") String viewerId,
                             @RequestParam(value = "limit", required = false) String limit,
                             @RequestParam(value = "offset", required = false) String offset,
                             @RequestParam(value = "direction", required = false) String direction,
                             @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getFavoriteEvents";
        LOGGER.debug("ViewerController :: " + operation + " :: viewerId :: " + viewerId + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = "";

        utils.isEmptyOrNull(viewerId, "viewerId");
        utils.isIntegerGreaterThanZero(viewerId, "viewerId");
        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Viewer.class);
                result = viewerService.getFavoriteEvents(Integer.valueOf(viewerId), Integer.valueOf(limit), Integer.valueOf(offset), direction,
                        orderBy);
            } else {
                result = viewerService.getFavoriteEvents(Integer.valueOf(viewerId));
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
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
                    0);
        }

        return result;
    }

    /**
     * This method is used to expose the REST API as GET to get Viewer's Purchased Events.
     *
     * @param requestBody: Request Body in JSON Format.
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/{viewerId}/purchased/event", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getPurchasedEvents(@PathVariable("viewerId") String viewerId,
                              @RequestParam(value = "limit", required = false) String limit,
                              @RequestParam(value = "offset", required = false) String offset,
                              @RequestParam(value = "direction", required = false) String direction,
                              @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getPurchasedEvents";
        LOGGER.debug("ViewerController :: " + operation + " :: viewerId :: " + viewerId + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = "";

        utils.isEmptyOrNull(viewerId, "viewerId");
        utils.isIntegerGreaterThanZero(viewerId, "viewerId");
        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Viewer.class);
                result = viewerService.getPurchasedEvents(Integer.valueOf(viewerId), Integer.valueOf(limit), Integer.valueOf(offset), direction,
                        orderBy);
            } else {
                result = viewerService.getPurchasedEvents(Integer.valueOf(viewerId));
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
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
                    0);
        }

        return result;
    }

    /**
     * This method is used to expose the REST API as PUT to change Viewer password.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated Viewer in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String changeViewerPassword(@RequestBody String requestBody,
                                @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "changeViewerPassword";
        LOGGER.info("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Viewer viewer = gson.fromJson(requestBody, Viewer.class);
        utils.isEmptyOrNull(viewer.getUsername(), "User Name");
        utils.isEmptyOrNull(viewer.getPassword(), "Password");
        JSONObject requestObj = new JSONObject(requestBody);
        utils.isEmptyOrNull(requestObj.has("oldPassword") ? requestObj.getString("oldPassword") : "", "Old Password");

        try {
            String oldPassword = new JSONObject(requestBody).getString("oldPassword");

            if (!viewerService.verifyOldPassword(viewer, oldPassword)) {
                throw new InvalidParameterException(IConstants.OLD_PASSWORD_DONOT_MATCH);
            }
            result = viewerService.changeViewerPassword(viewer);
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
     * This method is used to expose the REST API as PUT to update Viewer.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated Viewer in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateViewer(@RequestBody String requestBody,
                        @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateViewer";
        LOGGER.info("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Viewer viewer = gson.fromJson(requestBody, Viewer.class);
        utils.isEmptyOrNull(viewer.getId(), "id");
        utils.isIntegerGreaterThanZero(viewer.getId(), "id");
        // utils.isEmptyOrNull(viewer.getEmail(), "Email");
        utils.isAvailableObjectEmpty(viewer.getChatName(), "Chat Name");
        try {
            result = viewerService.updateViewer(viewer);
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

    @RequestMapping(value = "/chataccess/{eventId}/{access}/{accessFlag}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateViewerChatAccess(@RequestBody String requestBody,
                                  @RequestHeader("Content-Type") String contentType, @PathVariable("eventId") String eventId, @PathVariable("access") String access, @PathVariable("accessFlag") String accessFlag)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateViewerChatAccess";
        LOGGER.info("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType + " :: eventId :: " + eventId + " :: access :: " + access + " :: accessFlag :: " + accessFlag);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Viewer viewer = gson.fromJson(requestBody, Viewer.class);
//		utils.isEmptyOrNull(viewer.getId(), "id");
//		utils.isIntegerGreaterThanZero(viewer.getId(), "id");
        // utils.isEmptyOrNull(viewer.getEmail(), "Email");
        utils.isEmptyOrNull(eventId, "Event Id");
        utils.isIntegerGreaterThanZero(eventId, "Event Id");
        utils.isEmptyOrNull(viewer.getChatName(), "Chat Name");
        utils.isEmptyOrNull(access, "Access");
        utils.isEmptyOrNull(accessFlag, "Access Flag");
        utils.isAccessTypeValid(access);
        utils.isBoolean(accessFlag, "Access Flag");
        try {
            result = viewerService.updateViewerChatAccess(viewer, Integer.parseInt(eventId), access, Boolean.parseBoolean(accessFlag));
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
     * This method is used to expose the REST API as PUT to update Viewer Profile.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated Viewer in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/profile", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateViewerProfile(@RequestBody String requestBody,
                               @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateViewerProfile";
        LOGGER.info("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Viewer viewer = gson.fromJson(requestBody, Viewer.class);
        utils.isEmptyOrNull(viewer.getId(), "id");
        utils.isIntegerGreaterThanZero(viewer.getId(), "id");
        utils.isAvailableObjectEmpty(viewer.getChatName(), "Chat Name");
        // utils.isEmptyOrNull(viewer.getEmail(), "Email");
        try {
            result = viewerService.updateViewer(viewer);
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
     * This method is used to expose the REST API as PUT to update Viewer status.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated Viewer in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/status", method = RequestMethod.PUT, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateViewerStatus(@RequestBody String requestBody,
                              @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateViewerStatus";
        LOGGER.info("ViewerController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        Viewer viewer = gson.fromJson(requestBody, Viewer.class);
        utils.isEmptyOrNull(viewer.getId(), "id");
        utils.isIntegerGreaterThanZero(viewer.getId(), "id");
        utils.isEmptyOrNull(viewer.getStatus(), "Status");
        utils.isStatusValid(viewer.getStatus());
        try {
            result = viewerService.updateViewer(viewer);
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
     * This method is used to expose the REST API as DELETE to delete Viewer by Id.
     *
     * @param userId: String
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{userId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String deleteViewer(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "deleteViewer";
        LOGGER.info("ViewerController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isEmptyOrNull(userId, "UserId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = viewerService.deleteViewer(Integer.parseInt(userId));
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
