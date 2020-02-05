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

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.Favorite;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The FavoriteHelper class is the utility class to build and validate
 * FavoriteHelper
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class FavoriteHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteHelper.class);

	/*
	 * IViewerRepository Bean
	 */
	@Autowired
	private IViewerRepository viewerRepository;

	/*
	 * IBroadcasterInfoRepository Bean
	 */
	@Autowired
	private IBroadcasterInfoRepository broadcasterInfoRepository;

	/*
	 * IEventRepository Bean
	 */
	@Autowired
	private IEventRepository eventRepository;
	
	/**
	 * This method is used to create the Favorite Entity by copying properties from
	 * requested Bean
	 * 
	 * @param favoriteReqObj
	 *            : Favorite
	 * @return Favorite: favoriteEntity
	 * 
	 */
	public Favorite populateFavorite(Favorite favoriteReqObj) {
		Favorite favoriteEntity = new Favorite();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		favoriteEntity.setCreatedOn(favoriteReqObj.getCreatedOn() != null ? favoriteReqObj.getCreatedOn() : currentTime);
		favoriteEntity.setStatus(favoriteReqObj.getStatus() != null ? favoriteReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		if(favoriteReqObj.getBroadcasterInfo() != null) {
			Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(favoriteReqObj.getBroadcasterInfo().getId());
			if(broadcasterInfoEntity.isPresent())
				favoriteEntity.setBroadcasterInfo(broadcasterInfoEntity.get());
		}

		if(favoriteReqObj.getEvent() != null) {
			Optional<Event> eventEntity = eventRepository.findById(favoriteReqObj.getEvent().getId());
			if(eventEntity.isPresent())
				favoriteEntity.setEvent(eventEntity.get());
		}

		if(favoriteReqObj.getViewer() != null) {
			Optional<Viewer> viewerEntity = viewerRepository.findById(favoriteReqObj.getViewer().getId());
			if(viewerEntity.isPresent())
				favoriteEntity.setViewer(viewerEntity.get());
		}

		LOGGER.debug("Favorite Entity populated from Requested Favorite Object ");
		return favoriteEntity;
	}

//	/**
//	 * This method is used to copy the Favorite properties from requested Bean to
//	 * Entity Bean
//	 * 
//	 * @param favoriteReqObj
//	 *            : Favorite
//	 * @param favoriteEntity
//	 *            : Favorite
//	 * @return Favorite: favoriteEntity
//	 * 
//	 */
//	public Favorite populateFavorite(Favorite favoriteReqObj, Favorite favoriteEntity) {
//		favoriteEntity.setStatus(favoriteReqObj.getStatus() != null ? favoriteReqObj.getStatus() : favoriteEntity.getStatus());
//
//		LOGGER.debug("Favorite Entity populated from Requested Favorite Object ");
//		return favoriteEntity;
//	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param favorite:
	 *            Favorite
	 * @return JSONObject: favoriteObj
	 * 
	 * @throws JSONException
	 *             JSONException
	 * 
	 */
	public JSONObject buildResponseObject(Favorite favorite) throws JSONException {
		JSONObject favoriteObj = new JSONObject();
		favoriteObj.put("id", favorite.getId());
		favoriteObj.put("createdOn", favorite.getCreatedOn());
		if(favorite.getBroadcasterInfo() != null)
			favoriteObj.put("broadcasterInfo", new JSONObject().put("id", favorite.getBroadcasterInfo().getId()));
		if(favorite.getEvent() != null)
			favoriteObj.put("event", new JSONObject().put("id", favorite.getEvent().getId()));
		if(favorite.getViewer() != null)
			favoriteObj.put("viewer", new JSONObject().put("id", favorite.getViewer().getId()));

		LOGGER.debug("Favorite Response Object built for Favorite Object id :::: " + favorite.getId());
		return favoriteObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param favoriterList
	 *            : List&lt;Favorite&gt;
	 * @return JSONArray: favoriteArr
	 * 
	 * @throws JSONException
	 *             JSONException
	 */
	public JSONArray buildResponseObject(List<Favorite> favoriterList) throws JSONException {
		JSONArray favoriteArr = new JSONArray();
		for (Favorite favorite : favoriterList) {
			JSONObject favoriteObj = buildResponseObject(favorite);
			favoriteArr.put(favoriteObj);
		}
		LOGGER.debug("Favorite Response Array built with size :::: " + favoriteArr.length());
		return favoriteArr;
	}

}
