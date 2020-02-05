package com.yappyapps.spotlight.service;

import java.text.ParseException;

import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The AnalyticsService interface declares all the operations to act upon Genre
 * 
 * @author Priyanka Kathpal
 * @version 1.0
 * @since 2018-07-14
 */
public interface IAnalyticsService {
	/**
	 * This method is used to get top 5 events.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	
	public String getTop5Events(String startDate, String endDate, String country, Integer spotlightUserId)  throws ParseException, ResourceNotFoundException;
	
	/**
	 * This method is used to get top 5 broadcasters.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getTop5BroadCasters(String startDate, String endDate, String country)  throws ParseException, ResourceNotFoundException;
	/**
	 * This method is used to get count of events.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getEventsCount(String startDate, String endDate, String country,Integer spotlightUserId)throws ParseException, ResourceNotFoundException;
	/**
	 * This method is used to get count of broadcasters.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getBroadcastersCount(String startDate, String endDate, String country)throws ParseException, ResourceNotFoundException;
	/**
	 * This method is used to get count of viewers.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getViewersCount(String startDate, String endDate, String country)throws ParseException, ResourceNotFoundException;
	/**
	 * This method is used to get total revenue.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getTotalRevenue(String startDate, String endDate, String country, Integer spotlightUserId)throws ParseException, ResourceNotFoundException;

	/**
	 * This method is used to get total likes.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	
	public String getTotalLikes(String startDate, String endDate, String country, Integer spotlightUserId)throws ParseException, ResourceNotFoundException;

	/**
	 * This method is used to get total dislikes.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	
	public String getTotalDislikes(String startDate, String endDate, String country, Integer spotlightUserId)throws ParseException, ResourceNotFoundException;

	public String getGraphData(String startDate, String endDate, String country, Integer spotlightUserId) throws ParseException, ResourceNotFoundException;
	
}
