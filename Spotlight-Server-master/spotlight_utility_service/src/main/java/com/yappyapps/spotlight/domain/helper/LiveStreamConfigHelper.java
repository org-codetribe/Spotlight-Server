package com.yappyapps.spotlight.domain.helper;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.LiveStreamConfig;

/**
 * The LiveStreamConfigHelper class is the utility class to build and validate
 * LiveStreamConfig
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Component
public class LiveStreamConfigHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveStreamConfigHelper.class);

	/**
	 * This method is used to create the LiveStreamConfig Entity by copying
	 * properties from requested Bean
	 * 
	 * @param liveStreamConfigReqObj
	 *            : LiveStreamConfig
	 * @return LiveStreamConfig: liveStreamConfigEntity
	 * 
	 */
	public LiveStreamConfig populateLiveStreamConfig(LiveStreamConfig liveStreamConfigReqObj) {
		LiveStreamConfig liveStreamConfigEntity = new LiveStreamConfig();

		liveStreamConfigEntity.setConnectionType(
				liveStreamConfigReqObj.getConnectionType() != null ? liveStreamConfigReqObj.getConnectionType() : null);
		liveStreamConfigEntity
				.setPort(liveStreamConfigReqObj.getPort() != null ? liveStreamConfigReqObj.getPort() : null);
		liveStreamConfigEntity
				.setHost(liveStreamConfigReqObj.getHost() != null ? liveStreamConfigReqObj.getHost() : null);
		liveStreamConfigEntity.setPassword(
				liveStreamConfigReqObj.getPassword() != null ? liveStreamConfigReqObj.getPassword() : null);
		liveStreamConfigEntity.setUsername(
				liveStreamConfigReqObj.getUsername() != null ? liveStreamConfigReqObj.getUsername() : null);
		LOGGER.debug("LiveStreamConfig populated from Requested LiveStreamConfig Object ");
		return liveStreamConfigEntity;
	}

	/**
	 * This method is used to copy the LiveStreamConfig properties from requested
	 * Bean to Entity Bean
	 * 
	 * @param liveStreamConfigReqObj
	 *            : LiveStreamConfig
	 * @param liveStreamConfigEntity
	 *            : LiveStreamConfig
	 * @return LiveStreamConfig: liveStreamConfigEntity
	 * 
	 */
	public LiveStreamConfig populateLiveStreamConfig(LiveStreamConfig liveStreamConfigReqObj,
			LiveStreamConfig liveStreamConfigEntity) {
		liveStreamConfigEntity.setConnectionType(
				liveStreamConfigReqObj.getConnectionType() != null ? liveStreamConfigReqObj.getConnectionType()
						: liveStreamConfigEntity.getConnectionType());
		liveStreamConfigEntity.setPort(liveStreamConfigReqObj.getPort() != null ? liveStreamConfigReqObj.getPort()
				: liveStreamConfigEntity.getPort());
		liveStreamConfigEntity.setHost(liveStreamConfigReqObj.getHost() != null ? liveStreamConfigReqObj.getHost()
				: liveStreamConfigEntity.getHost());
		liveStreamConfigEntity
				.setPassword(liveStreamConfigReqObj.getPassword() != null ? liveStreamConfigReqObj.getPassword()
						: liveStreamConfigEntity.getPassword());
		liveStreamConfigEntity
				.setUsername(liveStreamConfigReqObj.getUsername() != null ? liveStreamConfigReqObj.getUsername()
						: liveStreamConfigEntity.getUsername());
		LOGGER.debug("LiveStreamConfig Entity populated from Requested LiveStreamConfig Object ");
		return liveStreamConfigEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param liveStreamConfig:
	 *            LiveStreamConfig
	 * @return JSONObject: liveStreamConfigObj
	 * 
	 */
	public JSONObject buildResponseObject(LiveStreamConfig liveStreamConfig) throws JSONException {
		JSONObject liveStreamConfigObj = new JSONObject();
		liveStreamConfigObj.put("id", liveStreamConfig.getId());
		liveStreamConfigObj.put("connectionType", liveStreamConfig.getConnectionType());
		liveStreamConfigObj.put("port", liveStreamConfig.getPort());
		liveStreamConfigObj.put("host", liveStreamConfig.getHost());
//		liveStreamConfigObj.put("password", liveStreamConfig.getPassword());
//		liveStreamConfigObj.put("username", liveStreamConfig.getUsername());
		if(liveStreamConfig.getConnectionType().equalsIgnoreCase("Server")) {
			liveStreamConfigObj.put("displayName", "Web");	
		} else {
			liveStreamConfigObj.put("displayName", "App");
		}
		LOGGER.debug("LiveStreamConfig Response Object built for LiveStreamConfig Object id :::: "
				+ liveStreamConfig.getId());
		return liveStreamConfigObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param liveStreamConfig:
	 *            LiveStreamConfig
	 * @return JSONObject: liveStreamConfigObj
	 * 
	 */
	public JSONObject buildResponseObjectInternal(LiveStreamConfig liveStreamConfig) throws JSONException {
		JSONObject liveStreamConfigObj = new JSONObject();
		liveStreamConfigObj.put("id", liveStreamConfig.getId());
		liveStreamConfigObj.put("connectionType", liveStreamConfig.getConnectionType());
		liveStreamConfigObj.put("port", liveStreamConfig.getPort());
		liveStreamConfigObj.put("host", liveStreamConfig.getHost());
		liveStreamConfigObj.put("password", liveStreamConfig.getPassword());
		liveStreamConfigObj.put("username", liveStreamConfig.getUsername());
		if(liveStreamConfig.getConnectionType().equalsIgnoreCase("Server")) {
			liveStreamConfigObj.put("displayName", "Web");	
		} else {
			liveStreamConfigObj.put("displayName", "App");
		}
		LOGGER.debug("LiveStreamConfig Response Object built for LiveStreamConfig Object id :::: "
				+ liveStreamConfig.getId());
		return liveStreamConfigObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param liveStreamConfigList
	 *            : List&lt;LiveStreamConfig&gt;
	 * @return JSONArray: liveStreamConfigArr
	 * 
	 */
	public JSONArray buildResponseObject(List<LiveStreamConfig> liveStreamConfigList) throws JSONException {
		JSONArray liveStreamConfigArr = new JSONArray();
		for (LiveStreamConfig liveStreamConfig : liveStreamConfigList) {
			liveStreamConfigArr.put(buildResponseObject(liveStreamConfig));
		}
		LOGGER.debug("LiveStreamConfig Response Array built with size :::: " + liveStreamConfigArr.length());
		return liveStreamConfigArr;
	}

}
