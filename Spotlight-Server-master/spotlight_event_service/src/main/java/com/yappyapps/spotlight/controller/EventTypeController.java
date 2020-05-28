package com.yappyapps.spotlight.controller;

import com.yappyapps.spotlight.util.AmazonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IEventTypeService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The EventTypeController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on EventType.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "1.0/eventtype")
public class EventTypeController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "EventType";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * IEventTypeService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IEventTypeService eventTypeService;

	/**
	 * Gson dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Gson gson;
	private AmazonClient amazonClient;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	public EventTypeController(AmazonClient amazonClient) {
		this.amazonClient = amazonClient;
	}

	/**
	 * This method is used to expose the REST API as POST to create EventType.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created EventType in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String createEventType(@RequestParam(value = "request") String requestBody,
			@RequestHeader("Content-Type") String contentType, @RequestPart("eventTypeImage") MultipartFile[] eventTypeImage, @RequestHeader(value="Authorization", required=false) String token)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createEventType";
		LOGGER.info("EventTypeController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		EventType eventType = gson.fromJson(requestBody, EventType.class);

		utils.isEmptyOrNull(eventType.getName(), "Name");
		utils.isStatusValid(eventType.getStatus());

		if (null != eventTypeImage && Arrays.asList(eventTypeImage).size() > 0) {
			Arrays.asList(eventTypeImage).stream().map(file -> {
				try {
					LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
					String url = this.amazonClient.uploadFile(file);
					eventType.setEventTypeBannerUrl(url);
					LOGGER.info("file URL :::: " + eventType.getEventTypeBannerUrl());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList());
		}

		try {
			result = eventTypeService.createEventType(eventType);
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
	 * This method is used to expose the REST API as GET to get all EventType with
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
	 * @return ResponseBody: EventTypes in JSON format.
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
	public @ResponseBody String getAllEventTypes(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllEventTypes";
		LOGGER.info("EventTypeController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, EventType.class);
				result = eventTypeService.getAllEventTypes(Integer.valueOf(limit), Integer.valueOf(offset), direction,
						orderBy);
			} else {
				result = eventTypeService.getAllEventTypes();
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
	 * This method is used to expose the REST API as GET to get EventType by Id.
	 * 
	 * @param eventTypeId:
	 *            String
	 * @return ResponseBody: EventType in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{eventTypeId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getEventTypeById(@PathVariable("eventTypeId") String eventTypeId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getEventTypeById";
		LOGGER.info("EventTypeController :: " + operation + " :: eventTypeId :: " + eventTypeId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(eventTypeId, "eventTypeId");
		utils.isIntegerGreaterThanZero(eventTypeId, "eventTypeId");
		try {
			result = eventTypeService.getEventType(Integer.parseInt(eventTypeId));
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
	 * This method is used to expose the REST API as GET to get all EventTypes by
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
	 * @return ResponseBody: EventTypes in JSON format.
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
	public @ResponseBody String getEventTypesByStatus(@PathVariable("status") String status,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getEventTypesByStatus";
		LOGGER.info("EventTypeController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
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
				utils.isOrderByPropertyValid(orderBy, EventType.class);

				result = eventTypeService.getEventTypeByStatus(status, Integer.valueOf(limit), Integer.valueOf(offset),
						direction, orderBy);
			} else {
				result = eventTypeService.getEventTypeByStatus(status);
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
	 * This method is used to expose the REST API as PUT to update EventType.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated EventType in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String updateEventType(@RequestParam(value = "request") String requestBody,
			@RequestHeader("Content-Type") String contentType, @RequestPart(value = "eventTypeImage",required = false) MultipartFile[] eventTypeImage)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateEventType";
		LOGGER.info("EventTypeController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		EventType eventType = gson.fromJson(requestBody, EventType.class);
		utils.isEmptyOrNull(eventType.getId(), "eventTypeId");
		utils.isIntegerGreaterThanZero(eventType.getId(), "eventTypeId");
		//utils.isAvailableObjectEmpty(eventType.getName(), "Name");
		if (null != eventTypeImage && Arrays.asList(eventTypeImage).size() > 0) {
			Arrays.asList(eventTypeImage).stream().map(file -> {
				try {
					LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
					String url = this.amazonClient.uploadFile(file);
					eventType.setEventTypeBannerUrl(url);
					LOGGER.info("file URL :::: " + eventType.getEventTypeBannerUrl());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList());
		}

		try {
			result = eventTypeService.updateEventType(eventType);
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
	 * This method is used to expose the REST API as DELETE to delete EventType by
	 * Id.
	 * 
	 * @param eventTypeId:
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
	@RequestMapping(value = "/id/{eventTypeId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteEventType(@PathVariable("eventTypeId") String eventTypeId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteEventType";
		LOGGER.info("EventTypeController :: " + operation + " :: eventTypeId :: " + eventTypeId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(eventTypeId, "eventTypeId");
		utils.isIntegerGreaterThanZero(eventTypeId, "eventTypeId");
		try {
			result = eventTypeService.deleteEventType(Integer.parseInt(eventTypeId));
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
