package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The ISpotlightUserService interface declares all the operations to act upon
 * EventType
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public interface IEventTypeService {
	/**
	 * This method is used to create the EventType
	 * 
	 * @param eventTypeReqObj:
	 *            EventType
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

	public String createEventType(EventType eventTypeReqObj) throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all EventTypes.
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

	public String getAllEventTypes() throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getAllEventTypes(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all EventTypes by id.
	 * 
	 * @param eventTypeId:
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

	public String getEventType(Integer eventTypeId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all EventTypes by status.
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

	public String getEventTypeByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all EventTypes by status with paging.
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

	public String getEventTypeByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the EventType.
	 * 
	 * @param eventTypeReqObj:
	 *            EventType
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

	public String updateEventType(EventType eventTypeReqObj)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to delete the Event by id.
	 * 
	 * @param eventTypeId:
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

	public String deleteEventType(Integer eventTypeId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;
}
