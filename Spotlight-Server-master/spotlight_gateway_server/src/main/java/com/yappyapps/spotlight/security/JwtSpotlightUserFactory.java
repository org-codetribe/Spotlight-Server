package com.yappyapps.spotlight.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.yappyapps.spotlight.domain.JwtSpotlightUser;
import com.yappyapps.spotlight.domain.SpotlightUser;

/**
 * The JwtSpotlightUserFactory class is the factory class to create
 * JwtSpotlightUser
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

public final class JwtSpotlightUserFactory {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSpotlightUserFactory.class);

	/**
	 * Constructor
	 */
	private JwtSpotlightUserFactory() {
	}

	/**
	 * This method is used to create JwtSpotlightUser from SpotlightUser
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * 
	 * @return JwtSpotlightUser: spotlightUser
	 */
	public static JwtSpotlightUser create(SpotlightUser spotlightUser) {
		LOGGER.info("JwtSpotlightUser created from spotlightUser");
		return new JwtSpotlightUser(spotlightUser.getId(), "_S" + spotlightUser.getUsername(), spotlightUser.getAddress1(),
				spotlightUser.getAddress2(), spotlightUser.getCity(), spotlightUser.getCountry(),
				spotlightUser.getName(), spotlightUser.getPaypalEmailId(), spotlightUser.getPhone(),
				spotlightUser.getState(), spotlightUser.getEmail(), spotlightUser.getPassword(),
				spotlightUser.getStatus(), spotlightUser.getUniqueName(), spotlightUser.getUserType(),
				spotlightUser.getZip(), mapToGrantedAuthorities(spotlightUser), spotlightUser.getCreatedOn(),
				spotlightUser.getUpdatedOn());
	}

	/**
	 * This method is used to map granted roles from SpotlightUser
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * 
	 * @return List&lt;GrantedAuthority&gt;
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities(SpotlightUser spotlightUser) {
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
		LOGGER.info("spotlightUser.getRoles() ::::::::::::::::::::::::::" + spotlightUser.getRoles().size());
		list = spotlightUser.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
				.collect(Collectors.toList());
		list.add(new SimpleGrantedAuthority("ROLE_" + spotlightUser.getUserType()));
		LOGGER.info("list ::::::::::::::::::::::::::" + list.toString());
		return list;
	}
}
