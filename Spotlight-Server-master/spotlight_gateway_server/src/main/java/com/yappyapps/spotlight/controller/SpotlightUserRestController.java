package com.yappyapps.spotlight.controller;

import javax.servlet.http.HttpServletRequest;

import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.service.IViewerService;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;
import com.yappyapps.spotlight.security.JwtAuthenticationRequest;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.JwtTokenUtil;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

@RestController
public class SpotlightUserRestController {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(SpotlightUserRestController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "SpotlightUserRest";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * tokenHeader
	 * <h1>@Value</h1> will enable auto inject the value from property file
	 */
	@Value("${jwt.header}")
	private String tokenHeader;

	/**
	 * JwtTokenUtil dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * ISpotlightUserService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserService spotlightUserService;
	@Autowired
	private IViewerService viewerService;
	@Autowired
	private ISpotlightUserRepository spotlightUserRepository;

	/**
	 * Gson dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Gson gson;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * This method is used to expose the REST API as POST to authenticate User and
	 * create the authentication token.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Authenticated SpotlightUser in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String createAuthenticationToken(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType, HttpServletRequest request)
			throws InvalidParameterException, SpotlightAuthenticationException, BusinessException {
		String operation = "createAuthenticationToken";
		LOGGER.info("SpotlightUserRestController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		
		LOGGER.info("getRemoteAddr :::::::::::::::: " + request.getRemoteAddr());

		JwtAuthenticationRequest authenticationRequest = gson.fromJson(requestBody, JwtAuthenticationRequest.class);
		utils.isEmptyOrNull(authenticationRequest.getUsername(), "Username");
		utils.isEmptyOrNull(authenticationRequest.getPassword(), "Password");
		authenticationRequest.setSourceIpAddress(request.getRemoteAddr());
		try {
			SpotlightUser spotlightUserEntity = null;
			String username = authenticationRequest.getUsername();
			if(username.startsWith("_S")) {
				username = authenticationRequest.getUsername().substring(2);
			}
			try {
				spotlightUserEntity = spotlightUserRepository.findByUsername(username);
			} catch (HibernateException | JpaSystemException | DataIntegrityViolationException sqlException) {
				LOGGER.error("User with username " + username + " does not exist. Retrieving with email.");
			}
			if(spotlightUserEntity != null) {
				result = spotlightUserService.createAuthenticationToken(authenticationRequest);
			}else {
				result = viewerService.createAuthenticationToken(authenticationRequest);
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (SpotlightAuthenticationException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (UsernameNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
					requestBody.length());
		}

		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get the authenticated
	 * user.
	 * 
	 * @param authorization:
	 *            String
	 * @return ResponseBody: Authenticated User in JSON format.
	 * 
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "api/1.0/tokenuser", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAuthenticatedUser(@RequestHeader("Authorization") String authorization)
			throws SpotlightAuthenticationException, BusinessException {
		String operation = "getAuthenticatedUser";
		LOGGER.info("SpotlightUserRestController :: " + operation + " :: authorization :: " + authorization);
		long startTime = System.currentTimeMillis();

		String result = "";
		if (authorization == null || !authorization.startsWith("Bearer "))
			throw new SpotlightAuthenticationException("Client is not Authorized.");

		String username = jwtTokenUtil.getUsernameFromToken(authorization.substring(7));

		try {
			result = spotlightUserService.getAuthenticatedUser(username);
		} catch (SpotlightAuthenticationException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (UsernameNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}

		return result;
	}

	/**
	 * This method is used to expose the REST API as PUT to reset SpotlightUser
	 * password.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated SpotlightUser in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "api/1.0/resetUserPassword", method = RequestMethod.PUT, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String resetUserPassword(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, SpotlightAuthenticationException, BusinessException {
		String operation = "resetUserPassword";
		LOGGER.info("SpotlightUserRestController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);
		SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);

		try {
			result = spotlightUserService.resetSpotlightUserPassword(spotlightUser);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (SpotlightAuthenticationException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (UsernameNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
					requestBody.length());
		}

		return result;
	}

	/**
	 * This method is used to expose the REST API as DELETE to logout user
	 * user.
	 * 
	 * @param authorization:
	 *            String
	 * @return ResponseBody: Success Response in JSON format.
	 * 
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "api/1.0/logout/user", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String logoutUser(@RequestHeader("Authorization") String authorization)
			throws SpotlightAuthenticationException, BusinessException {
		String operation = "logoutUser";
		LOGGER.info("SpotlightUserRestController :: " + operation + " :: authorization :: " + authorization);
		long startTime = System.currentTimeMillis();

		String result = "";
		if (authorization == null || !authorization.startsWith("Bearer "))
			throw new SpotlightAuthenticationException("Client is not Authorized.");

//		String username = jwtTokenUtil.getUsernameFromToken(authorization.substring(7));

		try {
			result = spotlightUserService.logoutUser(authorization.substring(7));
		} catch (SpotlightAuthenticationException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (UsernameNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}

		return result;
	}
}
