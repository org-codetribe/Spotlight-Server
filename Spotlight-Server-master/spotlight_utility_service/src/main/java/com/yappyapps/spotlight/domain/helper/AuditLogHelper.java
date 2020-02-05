package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.AuditLog;

/**
 * The AuditLogHelper class is the utility class to build and validate
 * AuditLog
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class AuditLogHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogHelper.class);

	/*
	 * SpotlightUserHelper Bean
	 */
	@Autowired
	private SpotlightUserHelper spotlightUserHelper;

	/**
	 * This method is used to create the AuditLog Entity by copying properties
	 * from requested Bean
	 * 
	 * @param auditLogReqObj:
	 *            AuditLog
	 * @return AuditLog: auditLogEntity.
	 * 
	 */
	public AuditLog populateAuditLog(AuditLog auditLogReqObj) {
		AuditLog auditLogEntity = new AuditLog();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		auditLogEntity.setOperation(auditLogReqObj.getOperation() != null ? auditLogReqObj.getOperation() : null);
		auditLogEntity.setCreatedOn(currentTime);
		auditLogEntity
				.setDescription(auditLogReqObj.getDescription() != null ? auditLogReqObj.getDescription() : null);
		auditLogEntity.setModule(auditLogReqObj.getModule() != null ? auditLogReqObj.getModule() : null);
		auditLogEntity.setSpotlightUser(auditLogReqObj.getSpotlightUser() != null ? auditLogReqObj.getSpotlightUser() : null);
		LOGGER.debug("AuditLog populated from Requested AuditLog Object ");
		return auditLogEntity;
	}

	/**
	 * This method is used to copy the AuditLog properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param auditLogReqObj
	 *            : AuditLog
	 * @param auditLogEntity
	 *            : AuditLog
	 * @return AuditLog: auditLogEntity
	 * 
	 */
	public AuditLog populateAuditLog(AuditLog auditLogReqObj, AuditLog auditLogEntity) {

		auditLogEntity
				.setOperation(auditLogReqObj.getOperation() != null ? auditLogReqObj.getOperation() : auditLogEntity.getOperation());
		auditLogEntity.setDescription(
				auditLogReqObj.getDescription() != null ? auditLogReqObj.getDescription() : auditLogEntity.getDescription());
		auditLogEntity.setModule(auditLogReqObj.getModule() != null ? auditLogReqObj.getModule()
				: auditLogEntity.getModule());
		auditLogEntity
		.setSpotlightUser(auditLogReqObj.getSpotlightUser() != null ? auditLogReqObj.getSpotlightUser() : auditLogEntity.getSpotlightUser());
		LOGGER.debug("AuditLog Entity populated from Requested AuditLog Object ");
		return auditLogEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param auditLog
	 *            : AuditLog
	 * @return JSONObject: auditLogObj
	 * @throws JSONException JSONException
	 */
	public JSONObject buildResponseObject(AuditLog auditLog) throws JSONException {
		JSONObject auditLogObj = new JSONObject();

		auditLogObj.put("id", auditLog.getId());
		auditLogObj.put("createdOn", auditLog.getCreatedOn());
		auditLogObj.put("operation", auditLog.getOperation());
		auditLogObj.put("description", auditLog.getDescription());
		auditLogObj.put("module", auditLog.getModule());
		auditLogObj.put("spotlightUser", spotlightUserHelper.buildResponseObject(auditLog.getSpotlightUser()));
		LOGGER.debug("AuditLog Response Object built for AuditLog Object id :::: " + auditLog.getId());
		return auditLogObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param auditLogList
	 *            : List&lt;AuditLog&gt;
	 * @return JSONArray: auditLogArr
	 * 
	 * @throws JSONException JSONException
	 */
	public JSONArray buildResponseObject(List<AuditLog> auditLogList)
			throws JSONException {
		JSONArray auditLogArr = new JSONArray();
		for (AuditLog auditLog : auditLogList) {
			auditLogArr.put(buildResponseObject(auditLog));
		}
		LOGGER.debug("AuditLog Response Array built with size :::: " + auditLogArr.length());
		return auditLogArr;
	}

}
