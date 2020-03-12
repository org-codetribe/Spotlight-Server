package com.yappyapps.spotlight.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventReview;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IACLService;
import com.yappyapps.spotlight.service.IEventService;
import com.yappyapps.spotlight.util.AmazonClient;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The EventController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on Event.
 *
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/event")
public class EventController {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    /**
     * Controller Name.
     */
    private static final String controller = "Event";

    /**
     * MeteringService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private MeteringService meteringService;

    /**
     * IEventService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventService eventService;

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
     * IACLService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IACLService aclService;

    /**
     * AmazonClient for AWS S3 operations
     */
    private AmazonClient amazonClient;

    /**
     * Constructor
     *
     * @param amazonClient: AmazonClient
     */
    @Autowired
    EventController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    /**
     * This method is used to expose the REST API as POST to create Event.
     *
     * @param requestBody:        Request Body in JSON Format
     * @param eventImage:         MultipartFile[]
     * @param contentType:        "application/json"
     * @param token:              Authorization Token
     * @param redirectAttributes: RedirectAttributes
     * @return ResponseBody: Created Event in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String createEvent(@RequestParam(value = "request") String requestBody,
                       @RequestPart("eventImage") MultipartFile[] eventImage, @RequestPart("eventVideo") MultipartFile[] eventVideo,
                       @RequestHeader("Content-Type") String contentType, @RequestHeader(value = "Authorization", required = false) String token, RedirectAttributes redirectAttributes)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "createEvent";
        LOGGER.info("EventController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType + " :: eventImage length:: " + eventImage.length + " :: token :: " + token);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        JSONObject reqJSON = new JSONObject(requestBody);

        //Added the below checks as creating ObjecliveStreamConfig = {LiveStreamConfig@13930} t from JSON throws an error if they are not Integer.
        if (reqJSON.has("totalSeats") && reqJSON.get("totalSeats") != null)
            utils.isInteger(reqJSON.get("totalSeats"), "Total Seats");
        if (reqJSON.has("eventDuration") && reqJSON.get("eventDuration") != null)
            utils.isInteger(reqJSON.get("eventDuration"), "Event Duration");

        Event event = gson.fromJson(requestBody, Event.class);
        if (event != null && event.getZip() != null && !event.getZip().trim().equals(""))
            utils.isInteger(event.getZip(), "Zip Code");

        JSONObject reqJson = new JSONObject(requestBody);
        if (reqJson.has("commission") && reqJson.get("commission") != null
                && !reqJson.get("commission").toString().equals("")) {
            utils.isFloat(reqJson.get("commission"), "Commission");
            Float commission = Float.parseFloat(reqJson.get("commission").toString());
            event.setCommission(commission);
        }
        utils.isEmptyOrNull(event.getDisplayName(), "Display Name");
        utils.isEmptyOrNull(event.getBroadcasterInfo(), "Broadcaster");
        utils.isEmptyOrNull(event.getBroadcasterInfo().getId(), "Broadcaster Id");
        utils.isIntegerGreaterThanZero(event.getBroadcasterInfo().getId(), "Broadcaster Id");
        utils.isEmptyOrNull(event.getPricingRule(), "Pricing Info");
        utils.isEmptyOrNull(event.getPricingRule().getId(), "Pricing Rule Id");
        utils.isIntegerGreaterThanZero(event.getPricingRule().getId(), "Pricing Rule Id");
        utils.isEmptyOrNull(event.getLiveStreamConfig(), "Live Stream Server");
        utils.isEmptyOrNull(event.getLiveStreamConfig().getId(), "Live Stream Server Id");
        utils.isIntegerGreaterThanZero(event.getLiveStreamConfig().getId(), "Live Stream Server Id");
        utils.isStatusValid(event.getStatus());
        utils.isEmptyOrNull(event.getTotalSeats(), "Total Seats");
        utils.isInteger(event.getTotalSeats(), "Total Seats");
        utils.isEmptyOrNull(event.getEventDuration(), "Event Duration");
        utils.isInteger(event.getEventDuration(), "Event Duration");
        utils.isEmptyOrNull(event.getActualPrice(), "Actual Price");


        if (null != eventImage && Arrays.asList(eventImage).size() > 0) {
            Arrays.asList(eventImage).stream().map(file -> {
                try {
                    LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
                    String url = this.amazonClient.uploadFile(file);
                    event.setEventImageUrl(url);
                    LOGGER.info("file URL :::: " + event.getEventImageUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }
        if (null != eventVideo && Arrays.asList(eventVideo).size() > 0) {
            Arrays.asList(eventVideo).stream().map(file -> {
                try {
                    LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
                    String url = this.amazonClient.uploadFile(file);
                    event.setEventVideoUrl(url);
                    LOGGER.info("file URL :::: " + event.getEventVideoUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }


        try {
            result = eventService.createEvent(event);
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

    /**
     * This method is used to expose the REST API as GET to get all Events by EventType with
     * paging.
     *
     * @param eventTypeId: String
     * @param limit:       String
     * @param offset:      String
     * @param direction:   String
     * @param orderBy:     String
     * @return ResponseBody: Events in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getAllEventsByEventType(@RequestParam(value = "eventTypeId", required = false) String eventTypeId, @RequestParam(value = "viewerId", required = false) String viewerId, @RequestParam(value = "brodcasterId", required = false) String brodcasterId,
                                   @RequestParam(value = "limit", required = false) String limit,
                                   @RequestParam(value = "offset", required = false) String offset,
                                   @RequestParam(value = "direction", required = false) String direction,
                                   @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAllEventsByEventType";
        LOGGER.info("EventController :: " + operation + " :: eventTypeId :: " + eventTypeId + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = null;
        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Event.class);
                if (eventTypeId != null && viewerId == null) {
                    result = eventService.getAllEvents(Integer.valueOf(eventTypeId), Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
                } else if (eventTypeId != null && viewerId != null) {
                    result = eventService.getAllEventsWithViewer(Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy, Integer.valueOf(viewerId), Integer.valueOf(eventTypeId));
                } else if (eventTypeId == null && viewerId != null) {
                    result = eventService.getAllEventsWithViewer(Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy, Integer.valueOf(viewerId), null);
                } else if (viewerId != null && brodcasterId != null) {
                    result = eventService.getEventsByBroadcaster(Integer.valueOf(viewerId),Integer.valueOf(brodcasterId),Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
                } else {
                    result = eventService.getAllEvents(Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
                }
            } else {
                if (eventTypeId != null && viewerId == null) {
                    result = eventService.getAllEvents(Integer.valueOf(eventTypeId));
                } else if (eventTypeId != null && viewerId != null) {
                    result = eventService.getAllEvents(Integer.valueOf(eventTypeId), Integer.valueOf(viewerId));
                } else if (eventTypeId == null && viewerId != null) {
                    result = eventService.getAllEvents(null, Integer.valueOf(viewerId));
                } else if(viewerId !=null && brodcasterId!=null){
                    result = eventService.getEventsByBroadcaster(Integer.valueOf(viewerId), Integer.valueOf(brodcasterId));
                }
                else {
                    result = eventService.getAllEvents();
                }
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
     * This method is used to expose the REST API as GET to get all Events by EventType Name with
     * paging.
     *
     * @param eventTypeName: String
     * @param limit:         String
     * @param offset:        String
     * @param direction:     String
     * @param orderBy:       String
     * @return ResponseBody: Events in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/public/eventType/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getAllEventsByEventTypeName(@RequestParam(value = "eventTypeName", required = false) String eventTypeName,
                                       @RequestParam(value = "limit", required = false) String limit,
                                       @RequestParam(value = "offset", required = false) String offset,
                                       @RequestParam(value = "direction", required = false) String direction,
                                       @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAllEventsByEventType";
        LOGGER.info("EventController :: " + operation + " :: eventTypeName :: " + eventTypeName + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = null;
        utils.isEmptyOrNull(eventTypeName, "eventTypeName");
        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Event.class);
                result = eventService.getAllEventsByEventTypeName(eventTypeName, Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
            } else {
                result = eventService.getAllEventsByEventTypeName(eventTypeName);
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
     * This method is used to expose the REST API as GET to get Event by Id.
     *
     * @param eventId: Integer
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{eventId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventById(@PathVariable("eventId") String eventId, @RequestParam(value = "viewerId", required = false) String viewerId, @RequestHeader(value = "Authorization", required = false) String token)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventById";
        LOGGER.info("EventController :: " + operation + " :: eventId :: " + eventId + " :: viewerId :: " + viewerId + " :: token :: " + token);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");
        Integer viewerIdVar = null;
        if (viewerId != null) {
            utils.isIntegerGreaterThanZero(viewerId, "viewerId");
            viewerIdVar = Integer.parseInt(viewerId);
        }
        try {
            BroadcasterInfo broadcasterInfo = aclService.getBroadcasterInfo(token);
            Viewer viewer = aclService.getViewer(token);
            SpotlightUser spotlightUser = aclService.getSpotlightUser(token);

            if (broadcasterInfo != null) {
                LOGGER.info("Get Event for Broadcaster Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar, broadcasterInfo);
            } else if (spotlightUser != null) {
                LOGGER.info("Get Event for Spotlight User Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar, spotlightUser);
            } else if (viewer != null) {
                LOGGER.info("Get Event for Viewer Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar, viewer);
            } else {
                LOGGER.info("Get Event for no Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar);
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
     * This method is used to expose the REST API as GET to get Event Live Stream Status by Id.
     *
     * @param eventId: Integer
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{eventId}/livestreamstate", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventLiveStreamState(@PathVariable("eventId") String eventId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventLiveStreamState";
        LOGGER.info("EventController :: " + operation + " :: eventId :: " + eventId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");
        try {
            result = eventService.getEventLiveStreamStatus(Integer.parseInt(eventId));
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
     * This method is used to expose the REST API as GET to get Event by name.
     *
     * @param eventName: String
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/name/{eventName}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventByName(@PathVariable("eventName") String eventName, @RequestHeader(value = "Authorization", required = false) String token)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventByName";
        LOGGER.info("EventController :: " + operation + " :: eventName :: " + eventName + " :: token :: " + token);
        long startTime = System.currentTimeMillis();


        String result = "";
        utils.isEmptyOrNull(eventName, "eventName");
        try {
            BroadcasterInfo broadcasterInfo = aclService.getBroadcasterInfo(token);
            result = eventService.getEventByName(eventName, broadcasterInfo);
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
     * This method is used to expose the REST API as GET to get Events by
     * Broadcaster User with paging and orderBy publicly.
     *
     * @param broadcasterId: String
     * @param viewerId:      String
     * @param limit:         String
     * @param offset:        String
     * @param direction:     String
     * @param orderBy:       String
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/public", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventsByBroadcasterPublic(@RequestParam(value = "limit", required = false) String limit,
                                        @RequestParam(value = "offset", required = false) String offset,
                                        @RequestParam(value = "direction", required = false) String direction,
                                        @RequestParam(value = "orderBy", required = false) String orderBy,
                                        @RequestParam(value = "viewerId", required = false) String viewerId,
                                        @RequestParam(value = "broadcasterId", required = false) String broadcasterId,
                                        @RequestParam(value = "status", required = false) String status)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventsByBroadcasterPublic";
        LOGGER.info("EventController :: " + operation + " :: broadcasterId :: " + broadcasterId + " :: viewerId :: " + viewerId + " :: limit :: "
                + limit + " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy + " :: status :: " + status);
        long startTime = System.currentTimeMillis();

        String result = "";
//		utils.isEmptyOrNull(broadcasterId, "broadcasterId");
//		utils.isIntegerGreaterThanZero(broadcasterId, "broadcasterId");
        Integer viewerIdVar = null;
        if (viewerId != null) {
            utils.isIntegerGreaterThanZero(viewerId, "viewerId");
            viewerIdVar = Integer.valueOf(viewerId);
        }

        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Event.class);
                if (broadcasterId != null) {
                    if (status != null) {
                        result = eventService.getEventsByBroadcaster(status, viewerIdVar, Integer.parseInt(broadcasterId), Integer.valueOf(limit),
                                Integer.valueOf(offset), direction, orderBy);
                    } else {
                        result = eventService.getEventsByBroadcaster(viewerIdVar, Integer.parseInt(broadcasterId), Integer.valueOf(limit),
                                Integer.valueOf(offset), direction, orderBy);
                    }
                } else {
                    if (status != null) {
                        result = eventService.getAllViewerEvents(status, viewerIdVar, Integer.valueOf(limit),
                                Integer.valueOf(offset), direction, orderBy);
                    } else {
                        result = eventService.getAllViewerEvents(viewerIdVar, Integer.valueOf(limit),
                                Integer.valueOf(offset), direction, orderBy);
                    }
                }
            } else {
                if (broadcasterId != null) {
                    if (status != null) {
                        result = eventService.getEventsByBroadcaster(status, viewerIdVar, Integer.parseInt(broadcasterId));
                    } else {
                        result = eventService.getEventsByBroadcaster(viewerIdVar, Integer.parseInt(broadcasterId));
                    }
                } else {
                    if (status != null) {
                        result = eventService.getAllViewerEvents(status, viewerIdVar);
                    } else {
                        result = eventService.getAllViewerEvents(viewerIdVar);
                    }
                }
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
     * This method is used to expose the REST API as GET to get Events by
     * Broadcaster User with paging and orderBy.
     *
     * @param broadcasterId: String
     * @param limit:         String
     * @param offset:        String
     * @param direction:     String
     * @param orderBy:       String
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/broadcaster/{broadcasterId}/{viewerId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventsByBroadcaster(@RequestParam(value = "limit", required = false) String limit,
                                  @RequestParam(value = "offset", required = false) String offset,
                                  @RequestParam(value = "direction", required = false) String direction,
                                  @RequestParam(value = "orderBy", required = false) String orderBy,
                                  @PathVariable(value = "broadcasterId") String broadcasterId,  @PathVariable(value = "viewerId") String viewerId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventsByBroadcaster";
        LOGGER.info("EventController :: " + operation + " :: broadcasterId :: " + broadcasterId + " :: limit :: "
                + limit + " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        utils.isEmptyOrNull(broadcasterId, "broadcasterId");
        utils.isEmptyOrNull(viewerId, "viewerId");
        String result = "";

        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Event.class);
                if (broadcasterId != null) {
                    result = eventService.getEventsByBroadcaster(Integer.parseInt(viewerId), Integer.parseInt(broadcasterId), Integer.valueOf(limit),
                            Integer.valueOf(offset), direction, orderBy);
                } else {
                    result = eventService.getAllViewerEvents(Integer.parseInt(viewerId), Integer.valueOf(limit),
                            Integer.valueOf(offset), direction, orderBy);
                }
            } else {
                if (broadcasterId != null && viewerId !=null) {
                    result = eventService.getEventsByBroadcaster(Integer.parseInt(viewerId), Integer.parseInt(broadcasterId));
                } else {
                    result = eventService.getAllEvents();
                }
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
     * This method is used to expose the REST API as GET to get Events by
     * status with paging and orderBy publicly.
     *
     * @param status:    String
     * @param viewerId:  String
     * @param limit:     String
     * @param offset:    String
     * @param direction: String
     * @param orderBy:   String
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/public/trending", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getTrendingEventsByStatus(@RequestParam(value = "limit", required = false) String limit,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "viewerId", required = false) String viewerId,
                                     @RequestParam(value = "offset", required = false) String offset,
                                     @RequestParam(value = "direction", required = false) String direction,
                                     @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getTrendingEventsByStatus";
        LOGGER.info("EventController :: " + operation + " :: status :: " + status + " :: viewerId :: " + viewerId + " :: limit :: "
                + limit + " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();

        String result = "";
//		utils.isEmptyOrNull(broadcasterId, "broadcasterId");
        Integer viewerIdVar = null;
        if (viewerId != null) {
            utils.isIntegerGreaterThanZero(viewerId, "viewerId");
            viewerIdVar = Integer.valueOf(viewerId);
        }

        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, Event.class);
                if (status != null) {
                    result = eventService.getTrendingEvents(status, viewerIdVar, Integer.valueOf(limit),
                            Integer.valueOf(offset), direction, orderBy);
                } else {
                    result = eventService.getTrendingEvents(viewerIdVar, Integer.valueOf(limit),
                            Integer.valueOf(offset), direction, orderBy);
                }
            } else {
                if (status != null) {
                    result = eventService.getTrendingEvents(status, viewerIdVar);
                } else {
                    result = eventService.getTrendingEvents(viewerIdVar);
                }
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
     * This method is used to expose the REST API as GET to get Event User by
     * Id publicly.
     *
     * @param eventId: String
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/public/id/{eventId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventByIdPublic(
            @PathVariable("eventId") String eventId, @RequestParam(value = "viewerId", required = false) String viewerId, @RequestHeader(value = "Authorization", required = false) String token)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventByIdPublic";
        LOGGER.info("BroadcasterInfoController :: " + operation + " :: eventId :: " + eventId + " :: viewerId :: " + viewerId + " :: token :: " + token);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");
        Integer viewerIdVar = null;
        if (viewerId != null) {
            utils.isIntegerGreaterThanZero(viewerId, "viewerId");
            viewerIdVar = Integer.parseInt(viewerId);
        }
        try {
            BroadcasterInfo broadcasterInfo = aclService.getBroadcasterInfo(token);
            Viewer viewer = aclService.getViewer(token);
            SpotlightUser spotlightUser = aclService.getSpotlightUser(token);

            if (broadcasterInfo != null) {
                LOGGER.info("Get Event for Broadcaster Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar, broadcasterInfo);
            } else if (spotlightUser != null) {
                LOGGER.info("Get Event for Spotlight User Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar, spotlightUser);
            } else if (viewer != null) {
                LOGGER.info("Get Event for Viewer Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar, viewer);
            } else {
                LOGGER.info("Get Event for no Session");
                result = eventService.getEvent(Integer.parseInt(eventId), viewerIdVar);
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
     * This method is used to expose the REST API as GET to get Events by status
     * with paging and orderBy.
     *
     * @param status:    String
     * @param limit:     String
     * @param offset:    String
     * @param direction: String
     * @param orderBy:   String
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getEventsByStatus(@PathVariable("status") String status,
                             @RequestParam(value = "limit", required = false) String limit,
                             @RequestParam(value = "offset", required = false) String offset,
                             @RequestParam(value = "direction", required = false) String direction,
                             @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getEventsByStatus";
        LOGGER.info("EventController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
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
                utils.isOrderByPropertyValid(orderBy, Event.class);
                result = eventService.getEventByStatus(status, Integer.valueOf(limit), Integer.valueOf(offset),
                        direction, orderBy);
            } else {
                result = eventService.getEventsByStatus(status);
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
     * This method is used to expose the REST API as POST to update Event.
     *
     * @param requestBody:        Request Body in JSON Format
     * @param eventImage:         MultipartFile[]
     * @param contentType:        "application/json"
     * @param token:              Authorization Token
     * @param redirectAttributes: RedirectAttributes
     * @return ResponseBody: Updated Event in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateEvent(@RequestPart(value = "request") String requestBody,
                       @RequestPart("eventImage") MultipartFile[] eventImage,
                       @RequestHeader("Content-Type") String contentType, @RequestHeader(value = "Authorization", required = false) String token, RedirectAttributes redirectAttributes)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "updateEvent";
        LOGGER.info("EventController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType + " :: eventImage length:: " + eventImage.length + " :: token :: " + token);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        JSONObject reqJSON = new JSONObject(requestBody);
        if (reqJSON.has("totalSeats") && reqJSON.get("totalSeats") != null)
            utils.isInteger(reqJSON.get("totalSeats"), "Total Seats");

        if (reqJSON.has("eventDuration") && reqJSON.get("eventDuration") != null)
            utils.isInteger(reqJSON.get("eventDuration"), "Event Duration");

        Event event = gson.fromJson(requestBody, Event.class);
        if (event != null && event.getZip() != null && !event.getZip().trim().equals(""))
            utils.isInteger(event.getZip(), "Zip Code");

        JSONObject reqJson = new JSONObject(requestBody);
        if (reqJson.has("commission") && reqJson.get("commission") != null && !reqJson.get("commission").toString().equals("")) {
            utils.isFloat(reqJson.get("commission"), "Commission");
            Float commission = Float.parseFloat(reqJson.get("commission").toString());
            event.setCommission(commission);
        }
        utils.isEmptyOrNull(event.getId(), "id");
        utils.isIntegerGreaterThanZero(event.getId(), "id");
        utils.isStatusValid(event.getStatus());
        utils.isAvailableObjectEmpty(event.getDisplayName(), "Display Name");
        if (event.getTotalSeats() != null)
            utils.isInteger(event.getTotalSeats(), "Total Seats");

        if (event.getEventDuration() != null)
            utils.isInteger(event.getEventDuration(), "Event Duration");

        if (null != eventImage && Arrays.asList(eventImage).size() > 0) {
            Arrays.asList(eventImage).stream().map(file -> {
                try {
                    LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
                    String url = this.amazonClient.uploadFile(file);
                    event.setEventImageUrl(url);
                    LOGGER.info("file URL :::: " + event.getEventImageUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }

        try {
            result = eventService.updateEvent(event);
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
     * This method is used to expose the REST API as PUT to update Event Status.
     *
     * @param requestBody: Request Body in JSON Format
     * @param contentType: "application/json"
     * @param token:       Authorization Token
     * @return ResponseBody: Updated Event in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/status", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateEventStatus(@RequestBody String requestBody,
                             @RequestHeader("Content-Type") String contentType, @RequestHeader(value = "Authorization", required = false) String token)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "updateEventStatus";
        LOGGER.info("EventController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType + " :: token :: " + token);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        Event event = gson.fromJson(requestBody, Event.class);

        utils.isEmptyOrNull(event.getId(), "id");
        utils.isIntegerGreaterThanZero(event.getId(), "id");
        utils.isStatusValid(event.getStatus());

        try {
            result = eventService.updateEvent(event);
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

    /**
     * This method is used to expose the REST API as PUT to review Event.
     *
     * @param requestBody: Request Body in JSON Format
     * @param contentType: "application/json"
     * @param token:       Authorization Token
     * @return ResponseBody: Updated Event in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/review", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String reviewEvent(@RequestBody String requestBody,
                       @RequestHeader("Content-Type") String contentType, @RequestHeader(value = "Authorization", required = false) String token)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "reviewEvent";
        LOGGER.info("EventController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType + " :: token :: " + token);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        EventReview eventReview = gson.fromJson(requestBody, EventReview.class);

        utils.isEmptyOrNull(eventReview.getEvent(), "Event");
        utils.isEmptyOrNull(eventReview.getEvent().getId(), "Event Id");
        utils.isEmptyOrNull(eventReview.getViewer(), "Viewer");
        utils.isEmptyOrNull(eventReview.getEvent().getId(), "Viewer Id");
        utils.isEmptyOrNull(eventReview.getIsLike(), "isLike");

        try {
            result = eventService.reviewEvent(eventReview);
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

    /**
     * This method is used to expose the REST API as GET to get Event Reviews by Id.
     *
     * @param eventId: Integer
     * @return ResponseBody: Event in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "reviews/id/{eventId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getReviewsByEventId(@PathVariable("eventId") String eventId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getReviewsByEventId";
        LOGGER.info("EventController :: " + operation + " :: eventId :: " + eventId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");
        try {
            result = eventService.getReviewsByEventId(Integer.parseInt(eventId));
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
     * This method is used to expose the REST API as DELETE to delete Event by Id.
     *
     * @param eventId: String
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{eventId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String deleteEvent(@PathVariable("eventId") String eventId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "deleteEvent";
        LOGGER.info("EventController :: " + operation + " :: eventId :: " + eventId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");

        try {
            result = eventService.deleteEvent(Integer.parseInt(eventId));
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
     * This method is used to expose the REST API as GET to get all Categories.
     *
     * @return ResponseBody: Categories in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/public/categories", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getAllCategories()
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAllCategories";
        LOGGER.info("EventController :: " + operation);
        long startTime = System.currentTimeMillis();
        String result = null;
        try {
            result = eventService.getAllCategories();
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

    @RequestMapping(value = "/public/testdate", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String testDate(@RequestBody String requestBody,
                    @RequestHeader("Content-Type") String contentType, @RequestHeader(value = "Authorization", required = false) String token)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "testdate";
        LOGGER.info("EventController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
                + contentType + " :: token :: " + token);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
        Event event = gson.fromJson(requestBody, Event.class);

        utils.isEmptyOrNull(event.getEventUtcDatetime(), "EventUtcDatetime");

        try {
            result = eventService.testDate(event);
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


    @RequestMapping(value = "/event-start/id/{eventId}/{spotlightUserId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String eventStart(@PathVariable("eventId") String eventId, @PathVariable("spotlightUserId") String spotlightUserId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "Event start Event";
        LOGGER.info("EventController :: " + operation + " :: eventId :: " + eventId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");
        utils.isEmptyOrNull(spotlightUserId, "spotlightUserId");
        utils.isIntegerGreaterThanZero(spotlightUserId, "spotlightUserId");
        try {
            //getEvent(Integer.parseInt(eventId),Integer.parseInt(spotlightUserId));
            result = eventService.getEventStart(Integer.parseInt(eventId), Integer.parseInt(spotlightUserId));
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


    @RequestMapping(value = "/event-stop/id/{eventId}/{spotlightUserId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String eventStop(@PathVariable("eventId") String eventId, @PathVariable("spotlightUserId") String spotlightUserId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "Event start Event";
        LOGGER.info("EventController :: " + operation + " :: eventId :: " + eventId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(eventId, "eventId");
        utils.isIntegerGreaterThanZero(eventId, "eventId");
        utils.isEmptyOrNull(spotlightUserId, "spotlightUserId");
        utils.isIntegerGreaterThanZero(spotlightUserId, "spotlightUserId");
        try {
            //getEvent(Integer.parseInt(eventId),Integer.parseInt(spotlightUserId));
            result = eventService.getEventStop(Integer.parseInt(eventId), Integer.parseInt(spotlightUserId));
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
