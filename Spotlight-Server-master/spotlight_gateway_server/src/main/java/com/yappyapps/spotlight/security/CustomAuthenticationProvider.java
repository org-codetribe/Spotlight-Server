package com.yappyapps.spotlight.security;

import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.JwtAuthToken;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.SpotlightUserSession;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.ViewerSession;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserSessionRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.repository.IViewerSessionRepository;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import com.yappyapps.spotlight.service.IViewerService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;



@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

	@Autowired
	private ISpotlightUserService spotlightUserService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private IViewerService viewerService;

	/**
	* ISpotlightUserRepository dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private ISpotlightUserRepository spotlightUserRepository;

	/**
	* IViewerRepository dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private IViewerRepository viewerRepository;

	/**
	 * ISpotlightUserSessionRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserSessionRepository spotlightUserSessionRepository;

	/**
	 * IViewerSessionRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IViewerSessionRepository viewerSessionRepository;

	/**
	* PasswordEncoder dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = "";
		if(authentication.getClass().equals(JwtAuthToken.class)) {
			String authToken = (String) authentication.getCredentials();
			
			try {
				jwtTokenUtil.verify(authToken);
			} catch (ExpiredJwtException eje) {
				try {
				spotlightUserSessionRepository.deleteByAuthToken(authToken);
				viewerSessionRepository.deleteByAuthToken(authToken);
				} catch (Exception e) {
					LOGGER.error("Error while deleting Session");
				}
				throw new SpotlightAuthenticationException("Session Expired. Please Login Again.", eje.getCause());
			}
			username = jwtTokenUtil.getUsernameFromToken((String) authentication.getCredentials());
			if (username != null) {
				LOGGER.debug("security context was null, so authorizating user");

			UserDetails userDetails = null;
			try {
				if(username.startsWith("_S")) {
					userDetails = this.spotlightUserService.loadUserByUsername(username);
					authentication = new SpotlightUsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
				} else if(username.startsWith("_V")) {
					userDetails = this.viewerService.loadUserByUsername(username);
					authentication = new ViewerUsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
				}
				
				 authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
			} catch (Exception e) {
				LOGGER.error("Username Not found.");
			}

		}

			if(username != null && username.startsWith("_S")) {
				SpotlightUserSession spotlightUserSession = spotlightUserSessionRepository.findByAuthToken(authToken);
				if(spotlightUserSession == null)
					throw new SpotlightAuthenticationException("Session Expired. Please Login Again.");
			} else if(username != null && username.startsWith("_V")) {
				ViewerSession viewerSession = viewerSessionRepository.findByAuthToken(authToken);
				if(viewerSession == null)
					throw new SpotlightAuthenticationException("Session Expired. Please Login Again.");
			}

		}
		
		username = authentication.getName();
		String password = (String) authentication.getCredentials();
		if (password != null && authentication.getClass().equals(SpotlightUsernamePasswordAuthenticationToken.class)) {
			LOGGER.info("SpotlightUsernamePasswordAuthenticationToken  :::::::::::::::::::::::::::::::::::::::::::: ");
			try {
				authenticateSpotlightUser(username, password);
			} catch (DisabledException e) {
				LOGGER.error(IConstants.ACCOUNT_DISABLED_MESSAGE);
				throw new SpotlightAuthenticationException(IConstants.ACCOUNT_DISABLED_MESSAGE);
			} catch (BadCredentialsException e) {
				LOGGER.error(IConstants.BAD_CREDENTIALS_MESSAGE);
				throw new SpotlightAuthenticationException(e.getMessage());
			}  catch (Exception e) {
				LOGGER.error(IConstants.CLIENT_NOT_AUTHORIZED_MESSAGE);
				throw new SpotlightAuthenticationException("Token Expired");
			}
			spotlightUserService.loadUserByUsername(username);
			return new SpotlightUsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
		} else if (password != null && authentication.getClass().equals(ViewerUsernamePasswordAuthenticationToken.class)) {
			LOGGER.info("ViewerUsernamePasswordAuthenticationToken  :::::::::::::::::::::::::::::::::::::::::::: ");
			try {
				authenticateViewer(username, password);
			} catch (DisabledException e) {
				LOGGER.error(IConstants.ACCOUNT_DISABLED_MESSAGE);
				throw new SpotlightAuthenticationException(IConstants.ACCOUNT_DISABLED_MESSAGE);
			} catch (BadCredentialsException e) {
				LOGGER.error(IConstants.BAD_CREDENTIALS_MESSAGE);
				throw new SpotlightAuthenticationException(e.getMessage());
			}  catch (Exception e) {
				LOGGER.error(IConstants.CLIENT_NOT_AUTHORIZED_MESSAGE);
				throw new SpotlightAuthenticationException("Token Expired");
			}
			viewerService.loadUserByUsername(username);
			return new ViewerUsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
		}
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (authentication.equals(SpotlightUsernamePasswordAuthenticationToken.class) || authentication.equals(ViewerUsernamePasswordAuthenticationToken.class) || authentication.equals(JwtAuthToken.class));
	}
	
	
	private void authenticateSpotlightUser(String username, String password) throws DisabledException, BadCredentialsException {
		SpotlightUser spotlightUserEntity = null;
		try {
			spotlightUserEntity = spotlightUserRepository.findByUsername(username);
		} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
			LOGGER.error("User with username " + username + " does not exist.");
		}
		
		if(spotlightUserEntity == null) {
			throw new BadCredentialsException("User with username " + username + " does not exist.");
		}
		
		if(spotlightUserEntity.getStatus().equalsIgnoreCase("Inactive")) {
			throw new DisabledException("User is disabled.");
		}

		if(!passwordEncoder.matches(password, spotlightUserEntity.getPassword())) {
			throw new BadCredentialsException(IConstants.BAD_CREDENTIALS_MESSAGE);
		}
		
		
	}
	
	private void authenticateViewer(String username, String password) throws DisabledException, BadCredentialsException {
		Viewer viewerEntity = null;
		try {
			viewerEntity = viewerRepository.findByUsername(username);
		} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
			LOGGER.error("User with username " + username + " does not exist.");
		}
		
		
		if(viewerEntity == null) {
			throw new BadCredentialsException("User with username " + username + " does not exist.");
		}
		
		if(viewerEntity.getStatus().equalsIgnoreCase("Inactive")) {
			throw new DisabledException("User is disabled.");
		}
		
		if(!passwordEncoder.matches(password, viewerEntity.getPassword())) {
			throw new BadCredentialsException(IConstants.BAD_CREDENTIALS_MESSAGE);
		}
		
	}
}