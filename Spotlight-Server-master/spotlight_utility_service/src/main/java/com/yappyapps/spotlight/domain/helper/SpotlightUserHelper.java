package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.*;

import com.yappyapps.spotlight.domain.Role;
import com.yappyapps.spotlight.repository.IRoleRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.repository.IAccessRoleRepository;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The SpotlightUserHelper class is the utility class to build and validate
 * Spotlight User
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class SpotlightUserHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightUserHelper.class);

	/*
	 * PasswordEncoder Bean
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/*
	 * IAccessRoleRepository Bean
	 */
	@Autowired
	private IAccessRoleRepository accessRoleRepository;

	@Autowired
	private IRoleRepository roleRepository;

	/**
	 * This method is used to create the SpotlightUser Entity by copying properties
	 * from requested Bean
	 * 
	 * @param spotlightUserReqObj
	 *            : SpotlightUser
	 * @return SpotlightUser: spotlightUserEntity
	 * 
	 */
	public SpotlightUser populateSpotlightUser(SpotlightUser spotlightUserReqObj) {
		SpotlightUser spotlightUserEntity = new SpotlightUser();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		spotlightUserEntity.setName(spotlightUserReqObj.getName() != null ? spotlightUserReqObj.getName() : null);
		spotlightUserEntity.setPhone(spotlightUserReqObj.getPhone() != null ? spotlightUserReqObj.getPhone() : null);
		spotlightUserEntity.setEmail(spotlightUserReqObj.getEmail() != null ? spotlightUserReqObj.getEmail() : null);
		spotlightUserEntity.setPassword(
				spotlightUserReqObj.getPassword() != null ? passwordEncoder.encode(spotlightUserReqObj.getPassword())
						: null);
		spotlightUserEntity
				.setAddress1(spotlightUserReqObj.getAddress1() != null ? spotlightUserReqObj.getAddress1() : null);
		spotlightUserEntity
				.setAddress2(spotlightUserReqObj.getAddress2() != null ? spotlightUserReqObj.getAddress2() : null);
		spotlightUserEntity.setCity(spotlightUserReqObj.getCity() != null ? spotlightUserReqObj.getCity() : null);
		spotlightUserEntity.setState(spotlightUserReqObj.getState() != null ? spotlightUserReqObj.getState() : null);
		spotlightUserEntity.setZip(spotlightUserReqObj.getZip() != null ? spotlightUserReqObj.getZip() : null);
		spotlightUserEntity
				.setCountry(spotlightUserReqObj.getCountry() != null ? spotlightUserReqObj.getCountry() : null);
		spotlightUserEntity.setPaypalEmailId(
				spotlightUserReqObj.getPaypalEmailId() != null ? spotlightUserReqObj.getPaypalEmailId() : null);
		spotlightUserEntity.setUserType(spotlightUserReqObj.getUserType() != null ? spotlightUserReqObj.getUserType()
				: IConstants.DEFAULT_USER_TYPE);
		spotlightUserEntity.setCreatedOn(
				spotlightUserReqObj.getCreatedOn() != null ? spotlightUserReqObj.getCreatedOn() : currentTime);
		spotlightUserEntity.setUpdatedOn(
				spotlightUserReqObj.getUpdatedOn() != null ? spotlightUserReqObj.getUpdatedOn() : currentTime);
		spotlightUserEntity.setUniqueName(Utils.generateRandomString(64));
		spotlightUserEntity.setStatus(
				spotlightUserReqObj.getStatus() != null ? spotlightUserReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		spotlightUserEntity
				.setUsername(spotlightUserReqObj.getUsername() != null ? spotlightUserReqObj.getUsername() : null);

		if (spotlightUserReqObj.getRoles() != null && spotlightUserReqObj.getRoles().size() > 0) {
			Iterator<AccessRole> itr = spotlightUserReqObj.getRoles().iterator();
			Set<AccessRole> roleList = new HashSet<AccessRole>();
			while (itr.hasNext()) {
				AccessRole userAccessRole = itr.next();
				Optional<AccessRole> accessRole = accessRoleRepository.findById(userAccessRole.getId());
				if (accessRole.isPresent())
					roleList.add(accessRole.get());
			}
			spotlightUserEntity.setRoles(roleList);
		}


		/*if (spotlightUserReqObj.getUserRoles() != null && spotlightUserReqObj.getUserRoles().size() > 0) {
			Iterator<Role> itr = spotlightUserReqObj.getUserRoles().iterator();
			List<Role> roleList = new ArrayList<>();
			while (itr.hasNext()) {
				Role userRole = itr.next();
				Optional<Role> role = roleRepository.findById(userRole.getId());
				if (role.isPresent())
					roleList.add(role.get());
			}
			spotlightUserEntity.setUserRoles(roleList);
		}
*/


		if (spotlightUserReqObj.getUsername() == null || spotlightUserReqObj.getUsername().trim().equals("")) {
			spotlightUserEntity.setUsername(spotlightUserReqObj.getEmail());
		}

		LOGGER.debug("SpotlightUser populated from Requested SpotlightUser Object ");
		return spotlightUserEntity;
	}

	/**
	 * This method is used to copy the SpotlightUser properties from requested Bean
	 * to Entity Bean
	 * 
	 * @param spotlightUserReqObj
	 *            : SpotlightUser
	 * @param spotlightUserEntity
	 *            : SpotlightUser
	 * @return SpotlightUser: spotlightUserEntity
	 * 
	 */
	public SpotlightUser populateSpotlightUser(SpotlightUser spotlightUserReqObj, SpotlightUser spotlightUserEntity) {
		Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

		spotlightUserEntity.setName(
				spotlightUserReqObj.getName() != null ? spotlightUserReqObj.getName() : spotlightUserEntity.getName());
		spotlightUserEntity.setPhone(spotlightUserReqObj.getPhone() != null ? spotlightUserReqObj.getPhone()
				: spotlightUserEntity.getPhone());
		spotlightUserEntity.setEmail(spotlightUserReqObj.getEmail() != null ? spotlightUserReqObj.getEmail()
				: spotlightUserEntity.getEmail());
		spotlightUserEntity.setPassword(
				spotlightUserReqObj.getPassword() != null ? passwordEncoder.encode(spotlightUserReqObj.getPassword())
						: spotlightUserEntity.getPassword());
		spotlightUserEntity.setAddress1(spotlightUserReqObj.getAddress1() != null ? spotlightUserReqObj.getAddress1()
				: spotlightUserEntity.getAddress1());
		spotlightUserEntity.setAddress2(spotlightUserReqObj.getAddress2() != null ? spotlightUserReqObj.getAddress2()
				: spotlightUserEntity.getAddress2());
		spotlightUserEntity.setCity(
				spotlightUserReqObj.getCity() != null ? spotlightUserReqObj.getCity() : spotlightUserEntity.getCity());
		spotlightUserEntity.setState(spotlightUserReqObj.getState() != null ? spotlightUserReqObj.getState()
				: spotlightUserEntity.getState());
		spotlightUserEntity.setZip(
				spotlightUserReqObj.getZip() != null ? spotlightUserReqObj.getZip() : spotlightUserEntity.getZip());
		spotlightUserEntity.setCountry(spotlightUserReqObj.getCountry() != null ? spotlightUserReqObj.getCountry()
				: spotlightUserEntity.getCountry());
		spotlightUserEntity.setPaypalEmailId(
				spotlightUserReqObj.getPaypalEmailId() != null ? spotlightUserReqObj.getPaypalEmailId()
						: spotlightUserEntity.getPaypalEmailId());
		spotlightUserEntity.setUserType(spotlightUserReqObj.getUserType() != null ? spotlightUserReqObj.getUserType()
				: spotlightUserEntity.getUserType());
		spotlightUserEntity.setUpdatedOn(updatedTime);
		spotlightUserEntity.setStatus(spotlightUserReqObj.getStatus() != null ? spotlightUserReqObj.getStatus()
				: spotlightUserEntity.getStatus());

		// if(spotlightUserReqObj.getRoles() != null &&
		// spotlightUserReqObj.getRoles().size() > 0) {
		// Iterator<AccessRole> itr = spotlightUserReqObj.getRoles().iterator();
		// Set<AccessRole> roleList = new HashSet<AccessRole>();
		// while(itr.hasNext()) {
		// AccessRole userAccessRole = itr.next();
		// Optional<AccessRole> accessRole =
		// accessRoleRepository.findById(userAccessRole.getId());
		// roleList.add(accessRole.get());
		// }
		// spotlightUserEntity.setRoles(roleList);
		// }
		LOGGER.debug("SpotlightUser Entity populated from Requested SpotlightUser Object ");
		return spotlightUserEntity;
	}

	/**
	 * This method is used to copy the SpotlightUser AccessRole properties from
	 * requested Bean to Entity Bean
	 * 
	 * @param accessRoleReqSet
	 *            : Set&lt;AccessRole&gt;
	 * @param spotlightUserEntity
	 *            : SpotlightUser
	 * @return SpotlightUser: spotlightUserEntity
	 * 
	 */
	public SpotlightUser populateSpotLightUserAccessRole(Set<AccessRole> accessRoleReqSet,
			SpotlightUser spotlightUserEntity) {
		Timestamp updatedTime = new Timestamp(System.currentTimeMillis());
		spotlightUserEntity.setUpdatedOn(updatedTime);
		if (accessRoleReqSet != null) {
			Iterator<AccessRole> itr = accessRoleReqSet.iterator();
			Set<AccessRole> roleList = new HashSet<AccessRole>();
			while (itr.hasNext()) {
				AccessRole userAccessRole = itr.next();
				Optional<AccessRole> accessRole = accessRoleRepository.findById(userAccessRole.getId());
				roleList.add(accessRole.get());
			}
			spotlightUserEntity.setRoles(roleList);
		}
		LOGGER.debug("SpotlightUser Entity populated from Requested AccessRole Object ");
		return spotlightUserEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @return JSONObject: spotlightUserObj
	 * 
	 * @throws JSONException
	 *             JSONException
	 * 
	 */
	public JSONObject buildResponseObject(SpotlightUser spotlightUser) throws JSONException {
		JSONObject spotlightUserObj = new JSONObject();
		spotlightUserObj.put("id", spotlightUser.getId());
		spotlightUserObj.put("address1", spotlightUser.getAddress1());
		spotlightUserObj.put("address2", spotlightUser.getAddress2());
		spotlightUserObj.put("city", spotlightUser.getCity());
		spotlightUserObj.put("country", spotlightUser.getCountry());
		spotlightUserObj.put("createdOn", spotlightUser.getCreatedOn());
		spotlightUserObj.put("email", spotlightUser.getEmail());
		spotlightUserObj.put("name", spotlightUser.getName());
		spotlightUserObj.put("paypalEmailId", spotlightUser.getPaypalEmailId());
		spotlightUserObj.put("phone", spotlightUser.getPhone());
		spotlightUserObj.put("state", spotlightUser.getState());
		spotlightUserObj.put("status", spotlightUser.getStatus());
		spotlightUserObj.put("uniqueName", spotlightUser.getUniqueName());
		spotlightUserObj.put("updatedOn", spotlightUser.getUpdatedOn());
		spotlightUserObj.put("userType", spotlightUser.getUserType());
		spotlightUserObj.put("username", spotlightUser.getUsername());
		spotlightUserObj.put("zip", spotlightUser.getZip());
		spotlightUserObj.put("roles", spotlightUser.getRoles());
		spotlightUserObj.put("token", spotlightUser.getToken());
		LOGGER.debug("SpotlightUser Response Object built for SpotlightUser Object id :::: " + spotlightUser.getId());
		return spotlightUserObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param spotlightUserList
	 *            : List&lt;SpotlightUser&gt;
	 * @return JSONArray: spotlightUserArr
	 * 
	 * @throws JSONException
	 *             JSONException
	 */
	public JSONArray buildResponseObject(List<SpotlightUser> spotlightUserList) throws JSONException {
		JSONArray spotlightUserArr = new JSONArray();
		for (SpotlightUser spotlightUser : spotlightUserList) {
			if (!spotlightUser.getUserType().equalsIgnoreCase("SUPERADMIN")) {
				JSONObject spotlightUserObj = buildResponseObject(spotlightUser);
				spotlightUserArr.put(spotlightUserObj);
			}
		}
		LOGGER.debug("SpotlightUser Response Array built with size :::: " + spotlightUserArr.length());
		return spotlightUserArr;
	}

}
