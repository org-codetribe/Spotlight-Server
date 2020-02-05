package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventReview;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;

/**
 * The EventReviewHelper class is the utility class to build and validate EventReview
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class EventReviewHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventReviewHelper.class);

	/*
	 * IEventRepository Bean
	 */
	@Autowired
	private IEventRepository eventRepository;

	/*
	 * IEventRepository Bean
	 */
	@Autowired
	private IViewerRepository viewerRepository;

	/**
	 * This method is used to create the EventReview Entity by copying properties from
	 * requested Bean
	 * 
	 * @param eventReviewReqObj
	 *            : EventReview
	 * @return EventReview: eventReviewEntity
	 * 
	 */
	public EventReview populateEventReview(EventReview eventReviewReqObj) {
		EventReview eventReviewEntity = new EventReview();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		eventReviewEntity.setComment(eventReviewReqObj.getComment() != null ? eventReviewReqObj.getComment() : null);
		eventReviewEntity.setCreatedOn(currentTime);
		eventReviewEntity.setIsLike(eventReviewReqObj.getIsLike() != null ? eventReviewReqObj.getIsLike() : null);

		if (eventReviewReqObj.getEvent() != null) {
			Optional<Event> event = eventRepository.findById(eventReviewReqObj.getEvent().getId());
			if (event.isPresent())
				eventReviewEntity.setEvent(event.get());
		}
		if (eventReviewReqObj.getViewer() != null) {
			Optional<Viewer> viewer = viewerRepository.findById(eventReviewReqObj.getViewer().getId());
			if (viewer.isPresent())
				eventReviewEntity.setViewer(viewer.get());
		}
		LOGGER.debug("EventReview populated from Requested EventReview Object ");
		return eventReviewEntity;
	}

	/**
	 * This method is used to copy the EventReview properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param eventReviewReqObj
	 *            : EventReview
	 * @param eventReviewEntity
	 *            : EventReview
	 * @return EventReview: eventReviewEntity
	 * 
	 */
	public EventReview populateEventReview(EventReview eventReviewReqObj, EventReview eventReviewEntity) {

		eventReviewEntity.setComment(eventReviewReqObj.getComment() != null ? eventReviewReqObj.getComment() : eventReviewEntity.getComment());
		eventReviewEntity.setIsLike(eventReviewReqObj.getIsLike() != null ? eventReviewReqObj.getIsLike() : eventReviewEntity.getIsLike());
		if (eventReviewReqObj.getEvent() != null) {
			Optional<Event> event = eventRepository.findById(eventReviewReqObj.getEvent().getId());
			if (event.isPresent())
				eventReviewEntity.setEvent(event.get());
		}

		if (eventReviewReqObj.getViewer() != null) {
			Optional<Viewer> viewer = viewerRepository.findById(eventReviewReqObj.getViewer().getId());
			if (viewer.isPresent())
				eventReviewEntity.setViewer(viewer.get());
		}
		LOGGER.debug("EventReview Entity populated from Requested EventReview Object ");
		return eventReviewEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param eventReview:
	 *            EventReview
	 * @return JSONObject: eventReviewObj
	 * 
	 */
	public JSONObject buildResponseObject(EventReview eventReview) throws JSONException {
		JSONObject eventReviewObj = new JSONObject();
		eventReviewObj.put("id", eventReview.getId());
		eventReviewObj.put("createdOn", eventReview.getCreatedOn());
		eventReviewObj.put("comment", eventReview.getComment());
		eventReviewObj.put("isLike", eventReview.getIsLike());
		if(eventReview.getEvent() != null)
			eventReviewObj.put("event", new JSONObject().put("id", eventReview.getEvent().getId()));
		if(eventReview.getViewer() != null)
			eventReviewObj.put("viewer", new JSONObject().put("id", eventReview.getViewer().getId()));

		LOGGER.debug("EventReview Response Object built for EventReview Object id :::: " + eventReview.getId());
		return eventReviewObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param eventReviewList
	 *            : List&lt;EventReview&gt;
	 * @return JSONArray: eventReviewArr
	 * 
	 */
	public JSONArray buildResponseObject(List<EventReview> eventReviewList) throws JSONException {
		JSONArray eventReviewArr = new JSONArray();
		for (EventReview eventReview : eventReviewList) {
			JSONObject eventReviewObj = buildResponseObject(eventReview);
			if (eventReviewObj != null)
				eventReviewArr.put(eventReviewObj);

		}
		LOGGER.debug("EventReview Response Array built with size :::: " + eventReviewArr.length());
		return eventReviewArr;
	}

}
