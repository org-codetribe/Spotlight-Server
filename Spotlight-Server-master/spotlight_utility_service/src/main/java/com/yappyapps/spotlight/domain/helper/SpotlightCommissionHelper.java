package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.SpotlightCommission;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The SpotlightCommissionHelper class is the utility class to build and
 * validate SpotlightCommission
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class SpotlightCommissionHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightCommissionHelper.class);

	/**
	 * This method is used to create the SpotlightCommission Entity by copying
	 * properties from requested Bean
	 * 
	 * @param spotlightCommissionReqObj
	 *            : SpotlightCommission
	 * @return SpotlightCommission: spotlightCommissionEntity
	 * 
	 */
	public SpotlightCommission populateSpotlightCommission(SpotlightCommission spotlightCommissionReqObj) {
		SpotlightCommission spotlightCommissionEntity = new SpotlightCommission();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		spotlightCommissionEntity.setPercentage(
				spotlightCommissionReqObj.getPercentage() != null ? spotlightCommissionReqObj.getPercentage() : null);
		spotlightCommissionEntity.setCreatedOn(currentTime);
		spotlightCommissionEntity.setUpdatedOn(currentTime);
		spotlightCommissionEntity
				.setStatus(spotlightCommissionReqObj.getStatus() != null ? spotlightCommissionReqObj.getStatus()
						: IConstants.DEFAULT_STATUS);
		spotlightCommissionEntity.setBroadcasterInfo(
				spotlightCommissionReqObj.getBroadcasterInfo() != null ? spotlightCommissionReqObj.getBroadcasterInfo()
						: null);
		spotlightCommissionEntity
				.setEvent(spotlightCommissionReqObj.getEvent() != null ? spotlightCommissionReqObj.getEvent() : null);
		LOGGER.debug("SpotlightCommission populated from Requested SpotlightCommission Object ");
		return spotlightCommissionEntity;
	}

	/**
	 * This method is used to copy the SpotlightCommission properties from requested
	 * Bean to Entity Bean
	 * 
	 * @param spotlightCommissionReqObj
	 *            : SpotlightCommission
	 * @param spotlightCommissionEntity
	 *            : SpotlightCommission
	 * @return SpotlightCommission: spotlightCommissionEntity
	 * 
	 */
	public SpotlightCommission populateSpotlightCommission(SpotlightCommission spotlightCommissionReqObj,
			SpotlightCommission spotlightCommissionEntity) {
		Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

		spotlightCommissionEntity.setPercentage(
				spotlightCommissionReqObj.getPercentage() != null ? spotlightCommissionReqObj.getPercentage()
						: spotlightCommissionEntity.getPercentage());
		spotlightCommissionEntity.setUpdatedOn(updatedTime);
		spotlightCommissionEntity
				.setStatus(spotlightCommissionReqObj.getStatus() != null ? spotlightCommissionReqObj.getStatus()
						: spotlightCommissionEntity.getStatus());
		spotlightCommissionEntity.setBroadcasterInfo(
				spotlightCommissionReqObj.getBroadcasterInfo() != null ? spotlightCommissionReqObj.getBroadcasterInfo()
						: spotlightCommissionEntity.getBroadcasterInfo());
		spotlightCommissionEntity
				.setEvent(spotlightCommissionReqObj.getEvent() != null ? spotlightCommissionReqObj.getEvent()
						: spotlightCommissionEntity.getEvent());
		LOGGER.debug("SpotlightCommission Entity populated from Requested SpotlightCommission Object ");
		return spotlightCommissionEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param spotlightCommission:
	 *            SpotlightCommission
	 * @return JSONObject: spotlightCommissionObj
	 * @throws JSONException JSONException
	 */
	public JSONObject buildResponseObject(SpotlightCommission spotlightCommission)
			throws JSONException {
		JSONObject spotlightCommissionObj = new JSONObject();
		spotlightCommissionObj.put("id", spotlightCommission.getId());
		spotlightCommissionObj.put("createdOn", spotlightCommission.getCreatedOn());
		spotlightCommissionObj.put("percentage", spotlightCommission.getPercentage());
		spotlightCommissionObj.put("status", spotlightCommission.getStatus());
		spotlightCommissionObj.put("updatedOn", spotlightCommission.getUpdatedOn());
		spotlightCommissionObj.put("broadcaster", spotlightCommission.getBroadcasterInfo());
		spotlightCommissionObj.put("event", spotlightCommission.getEvent());
		LOGGER.debug("SpotlightCommission Response Object built for SpotlightCommission Object id :::: "
				+ spotlightCommission.getId());
		return spotlightCommissionObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param spotlightCommissionList
	 *            : List&lt;SpotlightCommission&gt;
	 * @return JSONArray: spotlightCommissionArr
	 * @throws JSONException JSONException
	 */
	public JSONArray buildResponseObject(List<SpotlightCommission> spotlightCommissionList)
			throws JSONException {
		JSONArray spotlightCommissionArr = new JSONArray();
		for (SpotlightCommission spotlightCommission : spotlightCommissionList) {
			spotlightCommissionArr.put(buildResponseObject(spotlightCommission));
		}
		LOGGER.debug("SpotlightCommission Response Array built with size :::: " + spotlightCommissionArr.length());
		return spotlightCommissionArr;
	}

}
