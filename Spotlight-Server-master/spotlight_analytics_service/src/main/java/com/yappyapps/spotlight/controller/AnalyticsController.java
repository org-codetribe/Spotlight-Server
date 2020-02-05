package com.yappyapps.spotlight.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IAnalyticsService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;

/**
 * The AnalyticsController class is the controller which will expose the search
 * functionality.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Priyanka Kathpal
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/analytics")
public class AnalyticsController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "Analytics";

	/**
	 * IAnalyticsService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IAnalyticsService analyticsService;

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * This method is used to expose the REST API as GET to get top 5 Events.
	 * 
	 * @return ResponseBody: Events in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/events/top5", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getTop5Events(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "broadcasterId", required = false) Integer spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getTop5Events";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country + " :::: spotlightUserId :::: " + spotlightUserId);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getTop5Events(startDate, endDate, country, spotlightUserId);
		} catch (Exception e) {
			System.out.println(e);
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get Top 5 BroadCasters.
	 * 
	 * @return ResponseBody: BroadCasters in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/broadcasters/top5", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getTop5BroadCasters(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "country", required = false) String country)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getTop5BroadCasters";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getTop5BroadCasters(startDate, endDate, country);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get count of events.
	 * 
	 * @return ResponseBody: Count of events in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/events/count", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getEventsCount(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "broadcasterId", required = false) Integer spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getEventsCount";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country + " :::: spotlightUserId :::: " + spotlightUserId);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getEventsCount(startDate, endDate, country, spotlightUserId);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get count of
	 * broadCasters.
	 * 
	 * @return ResponseBody: Count of broadCasters in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/broadcasters/count", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getBroadcastersCount(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "country", required = false) String country)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getBroadcastersCount";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getBroadcastersCount(startDate, endDate, country);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get count of
	 * viewers.
	 * 
	 * @return ResponseBody: Count of viewers in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/viewers/count", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getViewersCount(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "country", required = false) String country)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getViewersCount";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getViewersCount(startDate, endDate, country);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get the total revenue .
	 * 
	 * @return ResponseBody: total revenue in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/total/revenue", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getTotalRevenue(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "broadcasterId", required = false) Integer spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getTotalRevenue";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country + " :::: spotlightUserId :::: " + spotlightUserId);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getTotalRevenue(startDate, endDate, country, spotlightUserId);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get the total likes .
	 * 
	 * @return ResponseBody: total revenue in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/total/likes", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getTotalLikes(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "broadcasterId", required = false) Integer spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getTotalLikes";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country + " :::: spotlightUserId :::: " + spotlightUserId);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getTotalLikes(startDate, endDate, country, spotlightUserId);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	
	/**
	 * This method is used to expose the REST API as GET to get the total dislikes .
	 * 
	 * @return ResponseBody: total revenue in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/total/dislikes", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getTotalDislikes(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "broadcasterId", required = false) Integer spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getTotalDislikes";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country + " :::: spotlightUserId :::: " + spotlightUserId);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getTotalDislikes(startDate, endDate, country, spotlightUserId);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	
	/**
	 * This method is used to expose the REST API as GET to get the data for graphs .
	 * 
	 * @return ResponseBody: total revenue in JSON format.
	 * 
	 * @throws InvalidParameterException InvalidParameterException
	 * @throws ResourceNotFoundException ResourceNotFoundException
	 * @throws BusinessException         BusinessException
	 * 
	 */
	@RequestMapping(value = "/getGraphData", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getGraphData(@RequestParam(value = "startDate") String startDate,
			@RequestParam(value = "endDate") String endDate, @RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "broadcasterId", required = false) Integer spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getGraphData";
		LOGGER.info("AnalyticsController :: " + operation + " :::: startDate :::: " + startDate + " :::: endDate :::: "
				+ endDate + " :::: country :::: " + country + " :::: spotlightUserId :::: " + spotlightUserId);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			result = analyticsService.getGraphData(startDate, endDate, country, spotlightUserId);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}
	
}
