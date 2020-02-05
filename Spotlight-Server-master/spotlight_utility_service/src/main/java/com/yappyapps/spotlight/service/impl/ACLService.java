package com.yappyapps.spotlight.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AccessDeniedException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.service.IACLService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * The ACLService contains all the operations to verify access by the user
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-10-01
 */
@Service
public class ACLService implements IACLService {

	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ACLService.class);
	/**
	 * JwtTokenUtil
	 */
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * ISpotlightUserRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserRepository spotlightUserRepository;

	/**
	 * IBroadcasterInfoRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IBroadcasterInfoRepository broadcasterInfoRepository;

	/**
	 * IViewerRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IViewerRepository viewerRepository;

	/**
	 * IEventRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IEventRepository eventRepository;

	/**
	* This method is used to verify the access.
	*  
	* @param token: String
	* @param event: Event
	* @return Boolean: Response
	* 
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	*/	
	public Boolean isAllowed(String token, Event event) throws AccessDeniedException, BusinessException, Exception {
		Boolean allowed = false;
		String username = null;
		String authToken = null;

		if(token != null  && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				if(username.startsWith("_S")) {
					username = username.substring(2);
					SpotlightUser spotlightUser = spotlightUserRepository.findByUsername(username);
					
					if(spotlightUser.getUserType().equalsIgnoreCase("SUPERADMIN") || spotlightUser.getUserType().equalsIgnoreCase("ADMIN")) {
						allowed = true;
						return allowed;
					}
					
					BroadcasterInfo broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUser);
					
					if(broadcasterInfo == null)
						throw new AccessDeniedException(IConstants.ACCESS_DENIED_MESSAGE);
					
					Optional<Event> eventEntity = eventRepository.findById(event.getId());
					if(eventEntity.isPresent() && eventEntity.get().getBroadcasterInfo().getId() == broadcasterInfo.getId()) {
						allowed = true;
					}
					
				} else if(username.startsWith("_V")) {
					username = username.substring(2);
					Viewer viewer = viewerRepository.findByUsername(username);
				}
				
			} catch (IllegalArgumentException e) {
				LOGGER.error("an error occured during getting username from token", e);
			} catch (ExpiredJwtException e) {
				LOGGER.warn("the token is expired and not valid anymore", e);
			}
		} else {
			LOGGER.warn("couldn't find bearer string, will ignore the header");
		}

		return allowed;
	}

	public BroadcasterInfo getBroadcasterInfo(String token) throws AccessDeniedException, BusinessException, Exception {
		String username = null;
		String authToken = null;
		BroadcasterInfo broadcasterInfo = null;
		if(token != null  && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				if(username.startsWith("_S")) {
					username = username.substring(2);
					SpotlightUser spotlightUser = spotlightUserRepository.findByUsername(username);
					
					broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUser);
				}
				
			} catch (IllegalArgumentException e) {
				LOGGER.error("an error occured during getting username from token", e);
			} catch (ExpiredJwtException e) {
				LOGGER.warn("the token is expired and not valid anymore", e);
			}
		} else {
			LOGGER.warn("couldn't find bearer string, will ignore the header");
		}

		return broadcasterInfo;
	}

	public SpotlightUser getSpotlightUser(String token) throws AccessDeniedException, BusinessException, Exception {
		String username = null;
		String authToken = null;
		SpotlightUser spotlightUser = null;
		if(token != null  && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				if(username.startsWith("_S")) {
					username = username.substring(2);
					spotlightUser = spotlightUserRepository.findByUsername(username);
				}
				
			} catch (IllegalArgumentException e) {
				LOGGER.error("an error occured during getting username from token", e);
			} catch (ExpiredJwtException e) {
				LOGGER.warn("the token is expired and not valid anymore", e);
			}
		} else {
			LOGGER.warn("couldn't find bearer string, will ignore the header");
		}

		return spotlightUser;
	}

	public Viewer getViewer(String token) throws AccessDeniedException, BusinessException, Exception {
		String username = null;
		String authToken = null;
		Viewer viewer = null;
		if(token != null  && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				if(username.startsWith("_V")) {
					username = username.substring(2);
					viewer = viewerRepository.findByUsername(username);
				}
				
			} catch (IllegalArgumentException e) {
				LOGGER.error("an error occured during getting username from token", e);
			} catch (ExpiredJwtException e) {
				LOGGER.warn("the token is expired and not valid anymore", e);
			}
		} else {
			LOGGER.warn("couldn't find bearer string, will ignore the header");
		}

		return viewer;
	}

}
