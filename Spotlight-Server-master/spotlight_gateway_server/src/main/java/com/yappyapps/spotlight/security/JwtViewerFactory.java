package com.yappyapps.spotlight.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.yappyapps.spotlight.domain.JwtViewer;
import com.yappyapps.spotlight.domain.Viewer;

/**
 * The JwtViewerFactory class is the factory class to create JwtViewer
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public final class JwtViewerFactory {

	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtViewerFactory.class);

	/**
	 * Constructor
	 */
	private JwtViewerFactory() {
	}

	/**
	 * This method is used to create JwtViewer from Viewer
	 * 
	 * @param viewer:
	 *            Viewer
	 * 
	 * @return JwtViewer: viewer
	 */
	public static JwtViewer create(Viewer viewer) {
		LOGGER.info("JwtViewer created from viewer");
		return new JwtViewer(viewer.getId(), "_V" + viewer.getUsername(), viewer.getFname(), viewer.getLname(),
				viewer.getPhone(), viewer.getEmail(), viewer.getPassword(), viewer.getStatus(), viewer.getUniqueName(),
				viewer.getChatName(), mapToGrantedAuthorities(), viewer.getCreatedOn(), viewer.getUpdatedOn());
	}

	/**
	 * This method is used to map Viewer roles
	 * 
	 * @return List&lt;GrantedAuthority&gt;
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities() {
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
		list.add(new SimpleGrantedAuthority("ROLE_VIEWER"));
		return list;
	}
}
