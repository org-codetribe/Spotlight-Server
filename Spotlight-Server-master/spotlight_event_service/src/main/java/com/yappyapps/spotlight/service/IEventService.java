package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventReview;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The ISpotlightUserService interface declares all the operations to act upon
 * Event
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
public interface IEventService {

	/**
	 * This method is used to create the Event
	 * 
	 * @param eventReqObj:
	 *            Event
	 * @return String: Response
	 * 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 * 
	 */
	public String createEvent(Event eventReqObj) throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all Events.
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
	public String getAllEvents() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by EventType.
	 * 
	 * @param eventTypeId:
	 * 				Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllEvents(Integer eventTypeId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by EventType Name.
	 * 
	 * @param eventTypeName:
	 * 				String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllEventsByEventTypeName(String eventTypeName) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events with paging.
	 * 
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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
	public String getAllEvents(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by EventType with paging.
	 * 
	 * @param eventTypeId:
	 * 				Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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
	public String getAllEvents(Integer eventTypeId, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	public String getAllEventsWithViewer(Integer limit, Integer offset, String direction, String orderBy,Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by EventType Name with paging.
	 * 
	 * @param eventTypeName:
	 * 				String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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
	public String getAllEventsByEventTypeName(String eventTypeName, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by event id.
	 * 
	 * @param eventId:
	 *            Integer
	 * @param viewerId:
	 *            Integer
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
	public String getEvent(Integer eventId, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;
	
	public String getEvent(Integer eventId, Integer viewerId, SpotlightUser spotlightUser) throws ResourceNotFoundException, BusinessException, Exception;
	
	public String getEvent(Integer eventId, Integer viewerId, BroadcasterInfo broadcasterInfo) throws ResourceNotFoundException, BusinessException, Exception;
	
	public String getEvent(Integer eventId, Integer viewerId, Viewer viewer) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to expose the REST API as GET to get Event by name.
	 * 
	 * @param eventName:
	 *            String
	 * @param broadcasterInfo:
	 *            BroadcasterInfo
	 * @return ResponseBody: Event in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	public String getEventByName(String eventName, BroadcasterInfo broadcasterInfo) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by Broadcaster.
	 * 
	 * @param broadcasterId:
	 *            Integer
	 * @param viewerId:
	 *            Integer
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
	public String getEventsByBroadcaster(Integer viewerId, Integer broadcasterId)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by broadcaster with paging.
	 * 
	 * @param broadcasterId:
	 *            Integer
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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
	public String getEventsByBroadcaster(Integer viewerId, Integer broadcasterId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by status.
	 * 
	 * @param status:
	 *            String
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
	public String getEventsByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Events by status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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
	public String getEventByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending Events.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getTrendingEvents(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending Events by status.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            Integer
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

	public String getTrendingEvents(String status, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending Events with paging and
	 * orderBy.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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

	public String getTrendingEvents(Integer viewerId, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending Events by status with
	 * paging and orderBy.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
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

	public String getTrendingEvents(String status, Integer viewerId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the Event.
	 * 
	 * @param eventReqObj:
	 *            Event
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
	public String updateEvent(Event eventReqObj) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to delete the Event by id.
	 * 
	 * @param eventId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String deleteEvent(Integer eventId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;

	/**
	 * This method is used to get all the Events by viewerId with paging.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllViewerEvents(Integer viewerId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;

	/**
	 * This method is used to get all the Events by viewerId.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllViewerEvents(Integer viewerId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;

	/**
	 * This method is used to get all the Events by viewerId and broadcasterId and status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            Integer
	 * @param broadcasterId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getEventsByBroadcaster(String status, Integer viewerId, Integer broadcasterId, Integer limit,
			Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Events by viewerId and broadcasterId and status.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            Integer
	 * @param broadcasterId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getEventsByBroadcaster(String status, Integer viewerId, Integer broadcasterId) throws ResourceNotFoundException, BusinessException, Exception;
	/**
	 * This method is used to get all the Events by viewerId and status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllViewerEvents(String status, Integer viewerId, Integer limit, Integer offset,
			String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception;


	/**
	 * This method is used to get all the Events by viewerId and status.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String getAllViewerEvents(String status, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to review the Event.
	 * 
	 * @param eventReview:
	 *            EventReview
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String reviewEvent(EventReview eventReview) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Event Reviews by event id.
	 * 
	 * @param eventId:
	 *            Integer
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
	public String getReviewsByEventId(Integer eventId) throws ResourceNotFoundException, BusinessException, Exception;

	public String getAllCategories() throws ResourceNotFoundException, BusinessException, Exception;

	public String getEventLiveStreamStatus(Integer eventId)
			throws ResourceNotFoundException, BusinessException, Exception;

	public String testDate(Event eventReqObj) throws ResourceNotFoundException, BusinessException, Exception;

	public String getAllEvents(Integer eventTypeId, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;


}
