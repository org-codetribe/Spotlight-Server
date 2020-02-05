package com.yappyapps.spotlight.domain.helper;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.domain.Role;
import com.yappyapps.spotlight.util.IConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * The AccessRoleHelper class is the utility class to build and validate
 * AccessRole
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class RoleHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleHelper.class);

	/**
	 * This method is used to create the AccessRole Entity by copying properties
	 * from requested Bean
	 * 
	 * @param role:
	 *            Role
	 * @return Role: accessRoleEntity.
	 * 
	 */
	public Role populateRole(Role role) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		role.setName(role.getName() != null ? role.getName() : null);
		role.setCreatedOn(currentTime);
		role.setUpdatedOn(currentTime);
		role.setStatus(
				role.getStatus() != null ? role.getStatus() : IConstants.DEFAULT_STATUS);
		LOGGER.debug("Role populated from Requested Role Object ");
		return role;
	}

	/**
	 * This method is used to copy the AccessRole properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param roleReqObj
	 *            : AccessRole
	 * @param roleEntity
	 *            : AccessRole
	 * @return AccessRole: accessRoleEntity
	 * 
	 */
	public Role populateRole(Role roleReqObj, Role roleEntity) {
		Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

		roleEntity
				.setName(roleReqObj.getName() != null ? roleReqObj.getName() : roleReqObj.getName());
		roleEntity.setUpdatedOn(updatedTime);
		roleEntity.setStatus(
				roleReqObj.getStatus() != null ? roleReqObj.getStatus() : roleReqObj.getStatus());

		LOGGER.debug("Role Entity populated from Requested Role Object ");
		return roleEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param role
	 *            : AccessRole
	 * @return JSONObject: accessRoleObj
	 * @throws JSONException JSONException
	 */
	public JSONObject buildResponseObject(Role role) throws JSONException {
		JSONObject roleObj = new JSONObject();

		roleObj.put("id", role.getId());
		roleObj.put("createdOn", role.getCreatedOn());
		roleObj.put("name", role.getName());
		roleObj.put("status", role.getStatus());
		roleObj.put("updatedOn", role.getUpdatedOn());
		LOGGER.debug("Role Response Object built for Role Object id :::: " + role.getId());
		return roleObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param roleList
	 *            : List&lt;AccessRole&gt;
	 * @return JSONArray: accessRoleArr
	 * 
	 * @throws JSONException JSONException
	 */
	public JSONArray buildResponseObject(List<Role> roleList)
			throws JSONException {
		JSONArray roleArr = new JSONArray();
		for (Role role : roleList) {
			roleArr.put(buildResponseObject(role));
		}
		LOGGER.debug("Role Response Array built with size :::: " + roleArr.length());
		return roleArr;
	}

}
