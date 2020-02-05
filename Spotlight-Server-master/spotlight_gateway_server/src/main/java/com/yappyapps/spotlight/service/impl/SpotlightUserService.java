package com.yappyapps.spotlight.service.impl;

import java.sql.Timestamp;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.JwtSpotlightUser;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.SpotlightUserSession;
import com.yappyapps.spotlight.domain.helper.SpotlightUserHelper;
import com.yappyapps.spotlight.exception.AccountDisabledException;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserSessionRepository;
import com.yappyapps.spotlight.security.JwtAuthenticationRequest;
import com.yappyapps.spotlight.security.JwtSpotlightUserFactory;
import com.yappyapps.spotlight.security.SpotlightUsernamePasswordAuthenticationToken;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.JwtTokenUtil;
import com.yappyapps.spotlight.util.Utils;

import io.jsonwebtoken.ExpiredJwtException;

/**
* The SpotlightUserService class is the implementation of ISpotlightUserService
* 
* <h1>@Service</h1> denotes that it is a service class
* * 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
@Service
@Order(1)
public class SpotlightUserService implements ISpotlightUserService {
	/**
	* Logger for the class.
	*/	
	private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightUserService.class);

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
	 * ISpotlightUserSessionRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserSessionRepository spotlightUserSessionRepository;

	/**
	* AuthenticationManager dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	* IEmailNotificationService dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private IEmailNotificationService emailNotificationService;

	/**
	* JwtTokenUtil dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	* PasswordEncoder dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	* SpotlightUserHelper dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private SpotlightUserHelper spotlightUserHelper;

	/**
	* Utils dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private Utils utils;

	/**
	 * This method is used to load the user by username.
	 * 
	 * @param username:
	 *            String
	 * @return UserDetails: Response
	 * 
	 * @throws UsernameNotFoundException
	 *             UsernameNotFoundException
	 */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	LOGGER.info("******************************loadUserByUsername * username*********************" + username);
		SpotlightUser spotlightUserEntity = null;
		if(username.startsWith("_S")) {
			username = username.substring(2);
		}
		
		try {
			spotlightUserEntity = spotlightUserRepository.findByUsername(username);
		} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
			LOGGER.error("User with username " + username + " does not exist. Retrieving with email.");
		}

		if(spotlightUserEntity == null) {
			try {
				spotlightUserEntity = spotlightUserRepository.findByEmail(username);
			} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
				LOGGER.error("User with email " + username + " does not exist.");
			}
		}
		
		if( spotlightUserEntity == null) {
			throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
		} 

        return JwtSpotlightUserFactory.create(spotlightUserEntity);
    }

	/**
	 * This method is used to create the authentication token.
	 * 
	 * @param authenticationRequest:
	 *            JwtAuthenticationRequest
	 * @return String: Response
	 * 
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws AccountDisabledException
	 *             AccountDisabledException
	 * @throws BusinessException
	 *             BusinessException
	 */
    @Override
    public String createAuthenticationToken(JwtAuthenticationRequest authenticationRequest) throws SpotlightAuthenticationException, AccountDisabledException, BusinessException {
    	String result = null;
    	
		try {
			this.authenticationManager.authenticate(new SpotlightUsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (DisabledException e) {
			LOGGER.error(IConstants.ACCOUNT_DISABLED_MESSAGE);
			throw new SpotlightAuthenticationException(IConstants.ACCOUNT_DISABLED_MESSAGE);
		} catch (BadCredentialsException e) {
			LOGGER.error(IConstants.BAD_CREDENTIALS_MESSAGE);
			throw new SpotlightAuthenticationException(e.getMessage());
		}


		final UserDetails userDetails = this.loadUserByUsername(authenticationRequest.getUsername());
		
		if(!(userDetails != null && ((JwtSpotlightUser)userDetails).getStatus() != null && utils.isActive(((JwtSpotlightUser)userDetails).getStatus())))
			throw new AccountDisabledException(IConstants.ACCOUNT_DISABLED_MESSAGE);
		
		Optional<SpotlightUser> spotlightUser = spotlightUserRepository.findById(((JwtSpotlightUser) userDetails).getId());
		
		SpotlightUserSession spotlightUserSessionEntity = spotlightUserSessionRepository.findBySpotlightUser(spotlightUser.get());
		
		if(spotlightUserSessionEntity != null) {
			
			boolean sessionDeleteFlag = false;
			if(authenticationRequest.getForceLogin()) {
				sessionDeleteFlag = true;
				spotlightUserSessionRepository.delete(spotlightUserSessionEntity);
			} else {
			
				try {
					jwtTokenUtil.verify(spotlightUserSessionEntity.getAuthToken());
				} catch (ExpiredJwtException eje) {
					try {
					spotlightUserSessionRepository.deleteByAuthToken(spotlightUserSessionEntity.getAuthToken());
					sessionDeleteFlag = true;
					} catch (Exception e) {
						LOGGER.error("Error while deleting Session");
					}
				}
			}
			if(!sessionDeleteFlag)
				throw new AlreadyExistException("Session for the user already exists.");
		}
		
		JSONObject authObj = utils.buildResponseObject(jwtTokenUtil, userDetails,true);
		
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		SpotlightUserSession spotlightUserSession = new SpotlightUserSession();
		spotlightUserSession.setCreatedOn(currentTime);
		spotlightUserSession.setSourceIpAddress(authenticationRequest.getSourceIpAddress());
		spotlightUserSession.setSpotlightUser(spotlightUser.get());
		spotlightUserSession.setStatus(IConstants.DEFAULT_STATUS);
		spotlightUserSession.setAuthToken(authObj.getString("token"));
		
		spotlightUserSessionRepository.save(spotlightUserSession);
					
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.AUTH, authObj);
		result = utils.constructSucessJSON(jObj);
		return result;
    }
    
    
	/**
	 * This method is used to get the details of authenticated user.
	 * 
	 * @param username:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws AccountDisabledException
	 *             AccountDisabledException
	 * @throws BusinessException
	 *             BusinessException
	 */
    @Override
    public String getAuthenticatedUser(String username) throws ResourceNotFoundException, AccountDisabledException, BusinessException {
    	String result = null;
		SpotlightUser spotlightUserEntity = null;
		if(username.startsWith("_S")) {
			username = username.substring(2);
		}

		try {
			spotlightUserEntity = spotlightUserRepository.findByUsername(username);
		} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
			LOGGER.error("User with username " + username + " does not exist. Retrieving with email.");
		}

		if(spotlightUserEntity == null) {
			try {
				spotlightUserEntity = spotlightUserRepository.findByEmail(username);
			} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
				LOGGER.error("User with email " + username + " does not exist.");
			}
		}
		
		if( spotlightUserEntity == null) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		} 

		if(!(spotlightUserEntity != null && spotlightUserEntity.getStatus() != null && utils.isActive(spotlightUserEntity.getStatus())))
			throw new AccountDisabledException(IConstants.ACCOUNT_DISABLED_MESSAGE);

		BroadcasterInfo broadcaster = broadcasterInfoRepository.findBySpotlightUser(spotlightUserEntity);
		
		JSONObject jObj = new JSONObject();
		JSONObject spotlightUserJObj = spotlightUserHelper.buildResponseObject(spotlightUserEntity);
		if(broadcaster != null) {
			spotlightUserJObj.put("name", broadcaster.getDisplayName());
			spotlightUserJObj.put("broadcasterInfoId", broadcaster.getId());
		}
		
		jObj.put(IConstants.USER, spotlightUserJObj);
		result = utils.constructSucessJSON(jObj);
		return result;
    }

	/**
	 * This method is used to reset the password of Spotlight User.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
    @Override
	public String resetSpotlightUserPassword(SpotlightUser spotlightUser) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		String generatedPassword = Utils.generateRandomPassword(10);
		SpotlightUser spotlightUserEntity = null;
		
		
		try {
			spotlightUserEntity = spotlightUserRepository.findByUsername(spotlightUser.getEmail());
		} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
			LOGGER.error("User with username " + spotlightUser.getUsername() + " does not exist. Retrieving with email.");
		}

		if(spotlightUserEntity == null) {
			try {
				spotlightUserEntity = spotlightUserRepository.findByEmail(spotlightUser.getEmail());
			} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
				LOGGER.error("User with email " + spotlightUser.getUsername() + " does not exist.");
			}
		}

		if( spotlightUserEntity == null) {
			throw new ResourceNotFoundException("User does not exist.");
		} 

		if(!(spotlightUserEntity != null && spotlightUserEntity.getStatus() != null && utils.isActive(spotlightUserEntity.getStatus())))
			throw new AccountDisabledException(IConstants.ACCOUNT_DISABLED_MESSAGE);

			LOGGER.info("generatedPassword    ::::: " + generatedPassword);
			spotlightUserEntity.setPassword(passwordEncoder.encode(generatedPassword));

		try {
			spotlightUserRepository.save(spotlightUserEntity);
			spotlightUserSessionRepository.deleteBySpotlightUser(spotlightUserEntity);
		} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(spotlightUser.getPassword() == null || spotlightUser.getPassword().trim().equalsIgnoreCase("")) {
			try {
				spotlightUserEntity.setPassword(generatedPassword);
				emailNotificationService.sendMimeMessage(spotlightUserEntity, IConstants.EMAIL_SUBJECT_PASSWORD_RESET);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JSONObject jObj = new JSONObject();
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to logout the Spotlight User.
	 * 
	 * @param token:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String logoutUser(String token)  throws ResourceNotFoundException, BusinessException, Exception {
    	String result = null;

    	spotlightUserSessionRepository.deleteByAuthToken(token);

		
		JSONObject jObj = new JSONObject();
//		jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUserEntity));
		result = utils.constructSucessJSON(jObj);
		return result;
		
	}
}
