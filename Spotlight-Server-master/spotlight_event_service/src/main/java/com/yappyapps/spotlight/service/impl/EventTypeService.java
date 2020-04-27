package com.yappyapps.spotlight.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.SpotlightUser;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.domain.helper.EventTypeHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IEventTypeRepository;
import com.yappyapps.spotlight.service.IEventTypeService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The EventTypeService class is the implementation of IEventTypeService
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Service
public class EventTypeService implements IEventTypeService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeService.class);

	/**
	 * IEventTypeRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IEventTypeRepository eventTypeRepository;


	/**
	 * EventTypeHelper dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private EventTypeHelper eventTypeHelper;
	
	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

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
	@Override
	public String createEventType(EventType eventTypeReqObj)
			throws AlreadyExistException, BusinessException, Exception {
		String result = null;
		

		if ((eventTypeRepository.findByName(eventTypeReqObj.getName()) != null)) {
			throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
		}
		EventType eventTypeEntity = new EventType();
		try {
			eventTypeEntity = eventTypeHelper.populateEventType(eventTypeReqObj);
			eventTypeEntity = eventTypeRepository.save(eventTypeEntity);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPE, eventTypeHelper.buildResponseObject(eventTypeEntity));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getAllEventTypes() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<EventType> eventTypeEntityList = null;
		try {
			eventTypeEntityList = (List<EventType>) eventTypeRepository.findAllByOrderByName();
			List<Object[]> objects = eventTypeRepository.countEventUpcoming();
			if (objects != null && objects.size() > 0) {
				List<EventType> eventTypes = new ArrayList<>(objects.size());
				for (Object[] o : objects) {
					Integer eventCount = Integer.valueOf(o[0].toString());
					Integer eventTypeId = Integer.valueOf(o[1].toString());
					Optional<EventType> eventType = eventTypeRepository.findById(eventTypeId);
					eventTypes.add(eventType.get());
				}
				if(eventTypeEntityList != null)
				eventTypeEntityList.clear();
				eventTypeEntityList = eventTypes;
			}



		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (eventTypeEntityList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPES, eventTypeHelper.buildResponseObject(eventTypeEntityList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all EventTypes with paging.
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
	@Override
	public String getAllEventTypes(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<EventType> eventTypeList = new ArrayList<EventType>();
		int pageNum = offset / limit;

		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<EventType> eventTypes = eventTypeRepository.findAll(pageableRequest);
			totalCount = eventTypes.getTotalElements();
			List<EventType> eventTypeEntities = eventTypes.getContent();
			for (EventType eventTypeEntity : eventTypeEntities) {
				EventType eventTypeDto = new EventType();
				BeanUtils.copyProperties(eventTypeEntity, eventTypeDto);
				eventTypeList.add(eventTypeDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (eventTypeList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPES, eventTypeHelper.buildResponseObject(eventTypeList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventTypeList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all EventType by id.
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
	@Override
	public String getEventType(Integer eventTypeId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<EventType> eventType = null;
		
		try {
			eventType = eventTypeRepository.findById(eventTypeId);
			if (!eventType.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPE, eventTypeHelper.buildResponseObject(eventType.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

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
	@Override
	public String getEventTypeByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		List<EventType> eventTypeList = null;
		
		try {
			eventTypeList = eventTypeRepository.findAllByStatusOrderByName(status);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (eventTypeList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPES, eventTypeHelper.buildResponseObject(eventTypeList));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getEventTypeByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<EventType> eventTypeList = new ArrayList<EventType>();
		int pageNum = offset / limit;

		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<EventType> eventTypes = eventTypeRepository.findAllByStatus(status, pageableRequest);
			totalCount = eventTypes.getTotalElements();
			List<EventType> eventTypeEntities = eventTypes.getContent();
			for (EventType eventTypeEntity : eventTypeEntities) {
				EventType eventTypeDto = new EventType();
				BeanUtils.copyProperties(eventTypeEntity, eventTypeDto);
				eventTypeList.add(eventTypeDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (eventTypeList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPES, eventTypeHelper.buildResponseObject(eventTypeList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventTypeList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String updateEventType(EventType eventTypeReqObj)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<EventType> eventTypeentity = null;
		try {
			eventTypeentity = eventTypeRepository.findById(eventTypeReqObj.getId());
			if (!eventTypeentity.isPresent()) {
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}
			eventTypeReqObj = eventTypeHelper.populateEventType(eventTypeReqObj, eventTypeentity.get());
			eventTypeRepository.save(eventTypeReqObj);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTTYPE, eventTypeHelper.buildResponseObject(eventTypeReqObj));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to delete the EventType by id.
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
	@Override
	public String deleteEventType(Integer eventTypeId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		Optional<EventType> eventType = null;
		try {
			eventType = eventTypeRepository.findById(eventTypeId);
			if (!eventType.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			eventTypeRepository.delete(eventType.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new InvalidParameterException("Event Type could not be deleted as it has Events associated with it.");
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		result = utils.constructSucessJSON(jObj);
		return result;
	}

}
