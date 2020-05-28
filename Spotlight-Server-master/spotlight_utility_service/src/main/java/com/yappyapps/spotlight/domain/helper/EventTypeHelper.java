package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.yappyapps.spotlight.domain.Viewer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.repository.IEventTypeRepository;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The EventTypeHelper class is the utility class to build and validate
 * EventType
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Component
public class EventTypeHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeHelper.class);

	/*
	 * IEventTypeRepository Bean
	 */
	@Autowired
	private IEventTypeRepository eventTypeRepository;

	/**
	 * This method is used to create the EventType Entity by copying properties from
	 * requested Bean
	 * 
	 * @param eventTypeReqObj
	 *            : EventType
	 * @return EventType: eventTypeEntity
	 * 
	 */
	public EventType populateEventType(EventType eventTypeReqObj) {
		EventType eventTypeEntity = new EventType();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		eventTypeEntity.setName(eventTypeReqObj.getName() != null ? eventTypeReqObj.getName() : null);
		eventTypeEntity.setCreatedOn(currentTime);
		eventTypeEntity.setStatus(
				eventTypeReqObj.getStatus() != null ? eventTypeReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		eventTypeEntity.setIsCategory(eventTypeReqObj.getIsCategory() != null ? eventTypeReqObj.getIsCategory() : false);
		eventTypeEntity.setEventTypeBannerUrl(eventTypeReqObj.getEventTypeBannerUrl() != null ? eventTypeReqObj.getEventTypeBannerUrl() : null);
		if (eventTypeReqObj.getEventType() != null) {
			Optional<EventType> parentEventType = eventTypeRepository.findById(eventTypeReqObj.getEventType().getId());
			if (parentEventType.isPresent())
				eventTypeEntity.setEventType(parentEventType.get());
		}
		LOGGER.debug("EventType populated from Requested EventType Object ");
		return eventTypeEntity;
	}

	/**
	 * This method is used to copy the EventType properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param eventTypeReqObj
	 *            : EventType
	 * @param eventTypeEntity
	 *            : EventType
	 * @return EventType: eventTypeEntity
	 * 
	 */
	public EventType populateEventType(EventType eventTypeReqObj, EventType eventTypeEntity) {

		eventTypeEntity
				.setName(eventTypeReqObj.getName() != null ? eventTypeReqObj.getName() : eventTypeEntity.getName());

		eventTypeEntity
				.setEventTypeBannerUrl(eventTypeReqObj.getEventTypeBannerUrl() != null ? eventTypeReqObj.getEventTypeBannerUrl() : eventTypeEntity.getEventTypeBannerUrl());
		eventTypeEntity.setEventType(eventTypeReqObj.getEventType() != null ? eventTypeReqObj.getEventType()
				: eventTypeEntity.getEventType());
		eventTypeEntity.setStatus(
				eventTypeReqObj.getStatus() != null ? eventTypeReqObj.getStatus() : eventTypeEntity.getStatus());
		eventTypeEntity.setIsCategory(eventTypeReqObj.getIsCategory() != null ? eventTypeReqObj.getIsCategory() : eventTypeEntity.getIsCategory());
		if (eventTypeReqObj.getEventType() != null && eventTypeReqObj.getEventType().getName() != null) {
			Optional<EventType> parentEventType = eventTypeRepository.findById(eventTypeReqObj.getEventType().getId());
			if (parentEventType.isPresent())
				eventTypeEntity.setEventType(parentEventType.get());
		}
		LOGGER.debug("EventType Entity populated from Requested EventType Object ");
		return eventTypeEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param eventType:
	 *            EventType
	 * @return JSONObject: eventTypeObj
	 * 
	 */
	public JSONObject buildResponseObject(EventType eventType) throws JSONException {
		JSONObject eventTypeObj = new JSONObject();
		eventTypeObj.put("id", eventType.getId());
		eventTypeObj.put("createdOn", eventType.getCreatedOn());
		eventTypeObj.put("name", eventType.getName());
		eventTypeObj.put("status", eventType.getStatus());
		eventTypeObj.put("isCategory", eventType.getIsCategory());
		eventTypeObj.put("eventTypeImage", eventType.getEventTypeBannerUrl());
		if (eventType.getEventType() != null) {
			eventTypeObj.put("eventType", new JSONObject().put("id",eventType.getEventType().getId()));
		}

		JSONArray childArr = new JSONArray();
		List<EventType> childEventTypeList = eventTypeRepository.findByEventType(eventType);
		for (EventType childEventType : childEventTypeList) {
			childArr.put(buildResponseObject(childEventType));
		}

		if (childArr.length() > 0)
			eventTypeObj.put(IConstants.CHILDREN, childArr);
		LOGGER.debug("EventType Response Object built for EventType Object id :::: " + eventType.getId());
		return eventTypeObj;

	}

	public JSONObject buildResponseObjectForViwer(EventType eventType, Viewer viewer) throws JSONException {
		JSONObject eventTypeObj = new JSONObject();
		eventTypeObj.put("id", eventType.getId());
		eventTypeObj.put("createdOn", eventType.getCreatedOn());
		eventTypeObj.put("name", eventType.getName());
		eventTypeObj.put("status", eventType.getStatus());
		eventTypeObj.put("isCategory", eventType.getIsCategory());
		eventTypeObj.put("eventTypeImage", eventType.getEventTypeBannerUrl());
		if (eventType.getEventType() != null) {
			eventTypeObj.put("eventType", new JSONObject().put("id",eventType.getEventType().getId()));
		}

		JSONArray childArr = new JSONArray();
		List<EventType> childEventTypeList = eventTypeRepository.findByEventType(eventType);
		for (EventType childEventType : childEventTypeList) {
			childArr.put(buildResponseObject(childEventType));
		}

		if (childArr.length() > 0)
			eventTypeObj.put(IConstants.CHILDREN, childArr);
		LOGGER.debug("EventType Response Object built for EventType Object id :::: " + eventType.getId());
		return eventTypeObj;

	}



	/**
	 * This method is used to build the response object.
	 * 
	 * @param eventTypeList
	 *            : List&lt;EventType&gt;
	 * @return JSONArray: eventTypeArr
	 * 
	 */
	public JSONArray buildResponseObject(List<EventType> eventTypeList) throws JSONException {
		JSONArray eventTypeArr = new JSONArray();
		for (EventType eventType : eventTypeList) {
			JSONObject eventTypeObj = buildResponseObject(eventType);
			if (eventTypeObj != null && eventType.getEventType() == null)
				eventTypeArr.put(eventTypeObj);

		}
		LOGGER.debug("EventType Response Array built with size :::: " + eventTypeArr.length());
		return eventTypeArr;
	}

}
