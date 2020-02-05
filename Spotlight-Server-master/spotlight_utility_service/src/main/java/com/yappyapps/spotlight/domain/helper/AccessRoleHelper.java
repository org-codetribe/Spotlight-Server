package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The AccessRoleHelper class is the utility class to build and validate
 * AccessRole
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class AccessRoleHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessRoleHelper.class);

	/**
	 * This method is used to create the AccessRole Entity by copying properties
	 * from requested Bean
	 * 
	 * @param accessRoleReqObj:
	 *            AccessRole
	 * @return AccessRole: accessRoleEntity.
	 * 
	 */
	public AccessRole populateAccessRole(AccessRole accessRoleReqObj) {
		AccessRole accessRoleEntity = new AccessRole();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		accessRoleEntity.setName(accessRoleReqObj.getName() != null ? accessRoleReqObj.getName() : null);
		accessRoleEntity.setCreatedOn(currentTime);
		accessRoleEntity.setUpdatedOn(currentTime);
		accessRoleEntity.setStatus(
				accessRoleReqObj.getStatus() != null ? accessRoleReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		accessRoleEntity
				.setPermissions(accessRoleReqObj.getPermissions() != null ? accessRoleReqObj.getPermissions() : null);
		accessRoleEntity.setType(accessRoleReqObj.getType() != null ? accessRoleReqObj.getType() : null);
		accessRoleEntity.setDependentRoles(accessRoleReqObj.getDependentRoles() != null ? accessRoleReqObj.getDependentRoles() : null);
		LOGGER.debug("AccessRole populated from Requested AccessRole Object ");
		return accessRoleEntity;
	}

	/**
	 * This method is used to copy the AccessRole properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param accessRoleReqObj
	 *            : AccessRole
	 * @param accessRoleEntity
	 *            : AccessRole
	 * @return AccessRole: accessRoleEntity
	 * 
	 */
	public AccessRole populateAccessRole(AccessRole accessRoleReqObj, AccessRole accessRoleEntity) {
		Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

		accessRoleEntity
				.setName(accessRoleReqObj.getName() != null ? accessRoleReqObj.getName() : accessRoleEntity.getName());
		accessRoleEntity.setUpdatedOn(updatedTime);
		accessRoleEntity.setStatus(
				accessRoleReqObj.getStatus() != null ? accessRoleReqObj.getStatus() : accessRoleEntity.getStatus());
		accessRoleEntity.setPermissions(accessRoleReqObj.getPermissions() != null ? accessRoleReqObj.getPermissions()
				: accessRoleEntity.getPermissions());
		accessRoleEntity
				.setType(accessRoleReqObj.getType() != null ? accessRoleReqObj.getType() : accessRoleEntity.getType());
		accessRoleEntity
		.setDependentRoles(accessRoleReqObj.getDependentRoles() != null ? accessRoleReqObj.getDependentRoles() : accessRoleEntity.getDependentRoles());
		LOGGER.debug("AccessRole Entity populated from Requested AccessRole Object ");
		return accessRoleEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param accessRole
	 *            : AccessRole
	 * @return JSONObject: accessRoleObj
	 * @throws JSONException JSONException
	 */
	public JSONObject buildResponseObject(AccessRole accessRole) throws JSONException {
		JSONObject accessRoleObj = new JSONObject();

		accessRoleObj.put("id", accessRole.getId());
		accessRoleObj.put("createdOn", accessRole.getCreatedOn());
		accessRoleObj.put("name", accessRole.getName());
		accessRoleObj.put("status", accessRole.getStatus());
		accessRoleObj.put("updatedOn", accessRole.getUpdatedOn());
		accessRoleObj.put("permissions", accessRole.getPermissions());
		accessRoleObj.put("type", accessRole.getType());
		accessRoleObj.put("dependentRoles", accessRole.getDependentRoles());
		LOGGER.debug("AccessRole Response Object built for AccessRole Object id :::: " + accessRole.getId());
		return accessRoleObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param accessRoleList
	 *            : List&lt;AccessRole&gt;
	 * @return JSONArray: accessRoleArr
	 * 
	 * @throws JSONException JSONException
	 */
	public JSONArray buildResponseObject(List<AccessRole> accessRoleList)
			throws JSONException {
		JSONArray accessRoleArr = new JSONArray();
		for (AccessRole accessRole : accessRoleList) {
			accessRoleArr.put(buildResponseObject(accessRole));
		}
		LOGGER.debug("AccessRole Response Array built with size :::: " + accessRoleArr.length());
		return accessRoleArr;
	}

}
