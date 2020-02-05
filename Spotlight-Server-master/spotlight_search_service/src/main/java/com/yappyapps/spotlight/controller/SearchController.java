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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.ISearchService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The SearchController class is the controller which will expose the
 * search functionality.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/search")
public class SearchController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "Search";

	/**
	 * ISearchService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISearchService searchService;

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
	 * This method is used to expose the REST API as GET to get all Genres with
	 * paging.
	 * 
	 * @param searchTerm:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Genres in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/fuzzy/{searchTerm}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String fuzzySearch(@PathVariable("searchTerm") String searchTerm, @RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "fuzzySearch";
		LOGGER.info("SearchController :: " + operation + " :: searchTerm :: " + searchTerm  + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		utils.isEmptyOrNull(searchTerm, "searchTerm");
		utils.isMinLengthValid(searchTerm, "searchTerm", 3);
		String result = null;
		
		if(offset == null)
			offset = "0";

		if(limit == null)
			limit = "6";
		
		if(direction == null)
			direction = "ASC";

		if(orderBy == null)
			orderBy = "id";

		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);

				result = searchService.fuzzySearch(searchTerm, Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
			} else {
				result = searchService.fuzzySearch(searchTerm);
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
	 * This method is used to expose the REST API as GET to get all Genres with
	 * paging.
	 * 
	 * @param searchTerm:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Genres in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/fuzzy/broadcasters/{searchTerm}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String fuzzySearchBroadcasters(@PathVariable("searchTerm") String searchTerm, @RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "fuzzySearchBroadcasters";
		LOGGER.info("SearchController :: " + operation + " :: searchTerm :: " + searchTerm  + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		utils.isEmptyOrNull(searchTerm, "searchTerm");
		utils.isMinLengthValid(searchTerm, "searchTerm", 3);
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);

				result = searchService.fuzzySearchBroadcasters(searchTerm, Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
			} else {
				result = searchService.fuzzySearchBroadcasters(searchTerm);
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
	 * This method is used to expose the REST API as GET to get all Genres with
	 * paging.
	 * 
	 * @param searchTerm:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Genres in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/fuzzy/events/{searchTerm}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String fuzzySearchEvents(@PathVariable("searchTerm") String searchTerm, @RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "fuzzySearchEvents";
		LOGGER.info("SearchController :: " + operation + " :: searchTerm :: " + searchTerm  + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		utils.isEmptyOrNull(searchTerm, "searchTerm");
		utils.isMinLengthValid(searchTerm, "searchTerm", 3);
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);

				result = searchService.fuzzySearchEvents(searchTerm, Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
			} else {
				result = searchService.fuzzySearchEvents(searchTerm);
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
