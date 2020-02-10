package com.yappyapps.spotlight.service.impl;

import java.sql.Timestamp;
import java.util.Optional;

import com.yappyapps.spotlight.domain.*;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import org.hibernate.HibernateException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.helper.ViewerHelper;
import com.yappyapps.spotlight.exception.AccountDisabledException;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.repository.IViewerSessionRepository;
import com.yappyapps.spotlight.security.JwtAuthenticationRequest;
import com.yappyapps.spotlight.security.JwtViewerFactory;
import com.yappyapps.spotlight.security.ViewerUsernamePasswordAuthenticationToken;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.service.IViewerService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.JwtTokenUtil;
import com.yappyapps.spotlight.util.Utils;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * The ViewerService class is the implementation of IViewerService
 *
 * <h1>@Service</h1> denotes that it is a service class *
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class ViewerService implements IViewerService {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerService.class);

    /**
     * IViewerRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerRepository viewerRepository;

    /**
     * IViewerSessionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerSessionRepository viewerSessionRepository;
    @Autowired
    private IBroadcasterInfoRepository broadcasterInfoRepository;

    /**
     * AuthenticationManager dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     * <h1>@Lazy</h1> will initialize the bean at runtime.
     */
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;


    @Autowired
    private ISpotlightUserRepository spotlightUserRepository;

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
     * ViewerHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ViewerHelper viewerHelper;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    @Override
    public String createViewer(Viewer viewer) throws BusinessException, Exception {
        String result = null;

        // boolean generatePasswordFlag = false;
        //
        // String generatedPassword = Utils.generateRandomPassword(10);
        // if(spotlightUser.getPassword() == null ||
        // spotlightUser.getPassword().trim().equalsIgnoreCase("")) {
        // generatePasswordFlag = true;
        // }
        // if(generatePasswordFlag) {
        // spotlightUser.setPassword(generatedPassword);
        // LOGGER.info("generatedPassword ::::: " + generatedPassword);
        // }

        if ((viewerRepository.findByEmail(viewer.getEmail()) != null)) {
            throw new AlreadyExistException("Email already exists.");
        }

        if (viewerRepository.findByChatName(viewer.getChatName()) != null) {
            throw new AlreadyExistException("Chat Name already exists.");
        }

        try {
            viewer = viewerHelper.populateViewer(viewer);
            viewer = viewerRepository.save(viewer);
        } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        // if(generatePasswordFlag) {
        // try {
        // spotlightUser.setPassword(generatedPassword);
        // emailNotificationService.sendMimeMessage(spotlightUser, "Spotlight Account
        // Credentials");
        // LOGGER.info("email sent for createSpotlightUser ::::: " );
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObject(viewer));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to load the user by username.
     *
     * @param username: String
     * @return UserDetails: Response
     * @throws UsernameNotFoundException UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("******************************loadUserByUsername * username*********************" + username);
        Viewer viewerEntity = null;
        if (username.startsWith("_V")) {
            username = username.substring(2);
        }

        try {
            viewerEntity = viewerRepository.findByUsername(username);
        } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
            LOGGER.error("User with username " + username + " does not exist. Retrieving with email.");
        }

        if (viewerEntity == null) {
            try {
                viewerEntity = viewerRepository.findByEmail(username);
            } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
                LOGGER.error("User with email " + username + " does not exist.");
            }
        }

        if (viewerEntity == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }

        return JwtViewerFactory.create(viewerEntity);
    }

    /**
     * This method is used to create the authentication token.
     *
     * @param authenticationRequest: JwtAuthenticationRequest
     * @return String: Response
     * @throws SpotlightAuthenticationException SpotlightAuthenticationException
     * @throws AccountDisabledException         AccountDisabledException
     * @throws BusinessException                BusinessException
     */
    @Override
    public String createAuthenticationToken(JwtAuthenticationRequest authenticationRequest)
            throws SpotlightAuthenticationException, AccountDisabledException, BusinessException {
        String result = null;

        try {
            this.authenticationManager.authenticate(new ViewerUsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            LOGGER.error(IConstants.ACCOUNT_DISABLED_MESSAGE);
            throw new SpotlightAuthenticationException(IConstants.ACCOUNT_DISABLED_MESSAGE);
        } catch (BadCredentialsException e) {
            LOGGER.error(IConstants.BAD_CREDENTIALS_MESSAGE);
            throw new SpotlightAuthenticationException(e.getMessage());
        }

        // Reload password post-security so we can generate the token
        final UserDetails userDetails = this.loadUserByUsername(authenticationRequest.getUsername());
        if (!(userDetails != null && ((JwtViewer) userDetails).getStatus() != null
                && utils.isActive(((JwtViewer) userDetails).getStatus())))
            throw new AccountDisabledException(IConstants.ACCOUNT_DISABLED_MESSAGE);


        Optional<Viewer> viewer = viewerRepository.findById(((JwtViewer) userDetails).getId());
        boolean isBroadCasterExist = false;
        SpotlightUser spotlightUser = null;
        if(viewer.isPresent()) {
           spotlightUser = spotlightUserRepository.findByEmail(viewer.get().getEmail());
            if(spotlightUser != null)
                isBroadCasterExist = true;
            BroadcasterInfo broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUser);
            spotlightUser.setId(broadcasterInfo.getId());


        }

        //ViewerSession viewerSessionEntity = viewerSessionRepository.findByViewer(viewer.get());
		
		/*if(viewerSessionEntity != null) {
			boolean sessionDeleteFlag = false;
			if(authenticationRequest.getForceLogin()) {
				sessionDeleteFlag = true;
				viewerSessionRepository.delete(viewerSessionEntity);
			} else {
				try {
					jwtTokenUtil.verify(viewerSessionEntity.getAuthToken());
				} catch (ExpiredJwtException eje) {
					try {
					viewerSessionRepository.deleteByAuthToken(viewerSessionEntity.getAuthToken());
					sessionDeleteFlag = true;
					} catch (Exception e) {
						LOGGER.error("Error while deleting Session");
					}
				}
			}
			if(!sessionDeleteFlag)
				throw new AlreadyExistException("Session for the viewer already exists.");

		}*/
        JSONObject authObj = utils.buildResponseObject(jwtTokenUtil, userDetails,spotlightUser);

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        ViewerSession viewerSession = new ViewerSession();
        viewerSession.setCreatedOn(currentTime);
        viewerSession.setSourceIpAddress(authenticationRequest.getSourceIpAddress());
        viewerSession.setViewer(viewer.get());
        viewerSession.setStatus(IConstants.DEFAULT_STATUS);
        viewerSession.setAuthToken(authObj.getString("token"));

       // viewerSessionRepository.save(viewerSession);

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.AUTH, authObj);
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get the details of authenticated user.
     *
     * @param username: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws AccountDisabledException  AccountDisabledException
     * @throws BusinessException         BusinessException
     */
    @Override
    public String getAuthenticatedUser(String username)
            throws ResourceNotFoundException, AccountDisabledException, BusinessException {
        String result = null;
        Viewer viewerEntity = null;
        if (username.startsWith("_V")) {
            username = username.substring(2);
        }

        try {
            viewerEntity = viewerRepository.findByUsername(username);
        } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
            LOGGER.error("User with username " + username + " does not exist. Retrieving with email.");
        }

        if (viewerEntity == null) {
            try {
                viewerEntity = viewerRepository.findByEmail(username);
            } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
                LOGGER.error("User with email " + username + " does not exist. Retrieving with phone.");
            }
        }

        if (viewerEntity == null) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        if (!(viewerEntity != null && viewerEntity.getStatus() != null && utils.isActive(viewerEntity.getStatus())))
            throw new AccountDisabledException(IConstants.ACCOUNT_DISABLED_MESSAGE);

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObject(viewerEntity));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to reset the password of Viewer.
     *
     * @param viewer: Viewer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String resetViewerPassword(Viewer viewer) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        String generatedPassword = Utils.generateRandomPassword(10);
        Viewer viewerEntity = null;
        SpotlightUser spotlightUserEntity = null;

        try {
            viewerEntity = viewerRepository.findByUsername(viewer.getEmail());
            if (viewerEntity != null)
                spotlightUserEntity = spotlightUserRepository.findByUsername(viewerEntity.getEmail());


        } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
            LOGGER.error("User with username " + viewer.getUsername() + " does not exist. Retrieving with email.");
        }

        if (viewerEntity == null) {
            try {
                viewerEntity = viewerRepository.findByEmail(viewer.getEmail());
                if (viewerEntity != null)
                    spotlightUserEntity = spotlightUserRepository.findByUsername(viewerEntity.getEmail());

            } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
                LOGGER.error("User with email " + viewer.getUsername() + " does not exist.");
            }
        }

        if (viewerEntity == null) {
            throw new ResourceNotFoundException("User does not exist.");
        }

        if (!(viewerEntity != null && viewerEntity.getStatus() != null && utils.isActive(viewerEntity.getStatus())))
            throw new AccountDisabledException(IConstants.ACCOUNT_DISABLED_MESSAGE);

        LOGGER.info("generatedPassword    ::::: " + generatedPassword);
        viewerEntity.setPassword(passwordEncoder.encode(generatedPassword));
        if (spotlightUserEntity != null)
            spotlightUserEntity.setPassword(passwordEncoder.encode(generatedPassword));

        try {
            viewerRepository.save(viewerEntity);
            viewerSessionRepository.deleteByViewer(viewerEntity);


            if (spotlightUserEntity != null) {
                spotlightUserRepository.save(spotlightUserEntity);
                //spotlightUserSessionRepository.deleteBySpotlightUser(spotlightUserEntity);
            }
        } catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (viewer.getPassword() == null || viewer.getPassword().trim().equalsIgnoreCase("")) {
            try {
                viewerEntity.setPassword(generatedPassword);
                emailNotificationService.sendMimeMessage(viewerEntity, IConstants.EMAIL_SUBJECT_PASSWORD_RESET);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jObj = new JSONObject();
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to logout the Viewer.
     *
     * @param token: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String logoutViewer(String token) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        viewerSessionRepository.deleteByAuthToken(token);


        JSONObject jObj = new JSONObject();
//		jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUserEntity));
        result = utils.constructSucessJSON(jObj);
        return result;

    }
}
