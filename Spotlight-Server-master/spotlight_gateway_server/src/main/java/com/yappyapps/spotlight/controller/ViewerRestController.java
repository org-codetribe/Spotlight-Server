package com.yappyapps.spotlight.controller;

import javax.naming.AuthenticationNotSupportedException;
import javax.servlet.http.HttpServletRequest;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;
import com.yappyapps.spotlight.security.JwtAuthenticationRequest;
import com.yappyapps.spotlight.service.IViewerService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.JwtTokenUtil;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

@RestController
public class 	ViewerRestController {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(ViewerRestController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "ViewerRest";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * tokenHeader
	 * <h1>@Value</h1> will enable auto inject the value from property file
	 */
	@Value("${jwt.header}")
	private String tokenHeader;
	@Autowired
	private IBroadcasterInfoRepository broadcasterInfoRepository;
	/**
	 * JwtTokenUtil dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	/**
	 * IViewerService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IViewerService viewerService;

	@Autowired
	private IViewerRepository iViewerRepository;

	@Autowired
	private ISpotlightUserRepository iSpotlightUserRepository;

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
	 * This method is used to expose the REST API as POST to register viewer.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created Viewer in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException	
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "${jwt.route.register.viewer.path}", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String registerViewer(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType) throws InvalidParameterException, BusinessException {
		String operation = "registerViewer";
		LOGGER.info("ViewerRestController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);

		Viewer viewer = gson.fromJson(requestBody, Viewer.class);
		utils.isEmptyOrNull(viewer.getEmail(), "Email");
		utils.isEmailValid(viewer.getEmail());
		utils.isEmptyOrNull(viewer.getChatName(), "Chat Name");
		utils.isEmptyOrNull(viewer.getPassword(), "Password");
		utils.isEmptyOrNull(viewer.getPhone(), "Phone");
		utils.isStatusValid(viewer.getStatus());
		try {
			result = viewerService.createViewer(viewer);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (SpotlightAuthenticationException e) {
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


	@RequestMapping(value = "/api/1.0/register/viewer", method = RequestMethod.PATCH, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String registerViewerWithSocial(@RequestBody String requestBody,
											   @RequestHeader("Content-Type") String contentType) throws InvalidParameterException, BusinessException, GeneralSecurityException, IOException {
		String operation = "registerViewerWithSocial";
		LOGGER.info("ViewerRestController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		final String CLIENT_ID = "459764271531-8dqve3rb1s271f94b1jke50ped5djkg4.apps.googleusercontent.com";

		String result = "";
		utils.isBodyJSONObject(requestBody);
		Viewer viewer = gson.fromJson(requestBody, Viewer.class);
		utils.isEmptyOrNull(viewer.getSocialLoginType(), "Social Login Type");
		if(viewer.getEmail() != null && viewer.getPhone() != null) {
			if (viewer.getEmail() == null)
				utils.isEmptyOrNull(viewer.getEmail(), "Email");
			if (viewer.getPhone() == null)
				utils.isEmptyOrNull(viewer.getPhone(), "Phone");
		}
		utils.isStatusValid(viewer.getStatus());
		try {
			if(viewer.getSocialLoginType().equalsIgnoreCase("FB")) {
				Viewer byEmailFB = iViewerRepository.findByEmail(viewer.getEmail());
				if(byEmailFB != null){
					SpotlightUser spotlightUser  = iSpotlightUserRepository.findByEmail(byEmailFB.getEmail());
					Boolean isBrodcast = false;
					if(spotlightUser != null){
						isBrodcast  = true;
						BroadcasterInfo broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUser);
						spotlightUser.setId(broadcasterInfo.getId());
					}


					UserDetails userDetailsbyEmail = viewerService.loadUserByUsername(byEmailFB.getUsername());
					JSONObject authObj = utils.buildResponseObject(jwtTokenUtil, userDetailsbyEmail,spotlightUser);
					JSONObject jObj = new JSONObject();
					jObj.put(IConstants.AUTH, authObj);
					result = utils.constructSucessJSON(jObj);
					return result;
				}else{
					String generatedPassword = Utils.generateRandomPassword(10);
					viewer.setEmail(viewer.getEmail());
					viewer.setUsername(viewer.getEmail());
					viewer.setPassword(passwordEncoder.encode(generatedPassword));
					viewer.setChatName(new Date().getTime() + "".trim());
				     viewerService.createViewer(viewer);
					UserDetails userDetailsbyEmail = viewerService.loadUserByUsername(viewer.getUsername());
					JSONObject authObj = utils.buildResponseObject(jwtTokenUtil, userDetailsbyEmail,null);
					JSONObject jObj = new JSONObject();
					jObj.put(IConstants.AUTH, authObj);
					result = utils.constructSucessJSON(jObj);
					meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
							requestBody.length());
					return result;
				}


			}else if(viewer.getSocialLoginType().equalsIgnoreCase("GMAIL")){
				/*com.google.api.client.json.JsonFactory jsonFactory = new JacksonFactory();
				HttpTransport transport = new NetHttpTransport();
				GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
						// Specify the CLIENT_ID of the app that accesses the backend:
						.setAudience(Collections.singletonList(CLIENT_ID))
						// Or, if multiple clients access the backend:
						//.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
						.build();

				// (Receive idTokenString by HTTPS POST)

				GoogleIdToken idToken = verifier.verify(viewer.getSocialLoginToken());
				if (idToken != null) {
					GoogleIdToken.Payload payload = idToken.getPayload();
					// Print user identifier
					String userId = payload.getSubject();
					System.out.println("User ID: " + userId);
					// Get profile information from payload
					String email = payload.getEmail();
					boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
					String name = (String) payload.get("name");
					String pictureUrl = (String) payload.get("picture");
					String locale = (String) payload.get("locale");
					String familyName = (String) payload.get("family_name");
					String givenName = (String) payload.get("given_name");*/
					String generatedPassword = Utils.generateRandomPassword(10);
					viewer.setEmail(viewer.getEmail());
					viewer.setUsername(viewer.getEmail());
					viewer.setPassword(passwordEncoder.encode(generatedPassword));
					//viewer.setProfilePicture(pictureUrl);
					Viewer byEmail = iViewerRepository.findByEmail(viewer.getEmail());

					if(byEmail != null){
						SpotlightUser spotlightUser  = iSpotlightUserRepository.findByEmail(byEmail.getEmail());
						Boolean isBrodcast = false;
						if(spotlightUser != null) {
							isBrodcast = true;
							BroadcasterInfo broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUser);
							spotlightUser.setId(broadcasterInfo.getId());
						}
						UserDetails userDetails = viewerService.loadUserByUsername(byEmail.getUsername());
						JSONObject authObj = utils.buildResponseObject(jwtTokenUtil, userDetails,spotlightUser);
						JSONObject jObj = new JSONObject();
						jObj.put(IConstants.AUTH, authObj);
						result = utils.constructSucessJSON(jObj);
						return result;
					}else{
						viewer.setChatName(new Date().getTime() + "".trim());
						viewerService.createViewer(viewer);


					//	Viewer viewer1 = iViewerRepository.findByEmail(viewer.getEmail());
						UserDetails userDetailsbyEmail =viewerService.loadUserByUsername(viewer.getUsername());
						JSONObject authObj = utils.buildResponseObject(jwtTokenUtil, userDetailsbyEmail,null);
						JSONObject jObj = new JSONObject();
						jObj.put(IConstants.AUTH, authObj);
						result = utils.constructSucessJSON(jObj);
						meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
								requestBody.length());
						return result;

					}
				}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (SpotlightAuthenticationException e) {
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
	 * This method is used to expose the REST API as POST to authenticate viewer and
	 * create the authentication token.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Authenticated Viewer in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "${jwt.route.authentication.viewer.path}", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String createAuthenticationToken(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType, HttpServletRequest request)
			throws InvalidParameterException, SpotlightAuthenticationException, BusinessException {
		String operation = "createAuthenticationToken";
		LOGGER.info("ViewerRestController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isBodyJSONObject(requestBody);

		LOGGER.info("getRemoteAddr :::::::::::::::: " + request.getRemoteAddr());

		JwtAuthenticationRequest authenticationRequest = gson.fromJson(requestBody, JwtAuthenticationRequest.class);
		utils.isEmptyOrNull(authenticationRequest.getUsername(), "Username");
		utils.isEmptyOrNull(authenticationRequest.getPassword(), "Password");
		authenticationRequest.setSourceIpAddress(request.getRemoteAddr());
		try {
			result = viewerService.createAuthenticationToken(authenticationRequest);
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
	 * viewer.
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
	@RequestMapping(value = "api/1.0/tokenviewer", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAuthenticatedUser(@RequestHeader("Authorization") String authorization)
			throws SpotlightAuthenticationException, BusinessException {
		String operation = "getAuthenticatedUser";
		LOGGER.info("ViewerRestController :: " + operation + " :: authorization :: " + authorization);
		long startTime = System.currentTimeMillis();
		String result = "";
		if (authorization == null || !authorization.startsWith("Bearer "))
			throw new SpotlightAuthenticationException("Client is not Authorized.");

		String username = jwtTokenUtil.getUsernameFromToken(authorization.substring(7));

		try {
			result = viewerService.getAuthenticatedUser(username);
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
	 * This method is used to expose the REST API as PUT to reset Viewer password.
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
	@RequestMapping(value = "api/1.0/resetViewerPassword", method = RequestMethod.PUT, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String resetViewerPassword(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, SpotlightAuthenticationException, BusinessException {
		String operation = "resetViewerPassword";
		LOGGER.info("ViewerRestController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		Viewer viewer = gson.fromJson(requestBody, Viewer.class);

		try {
			result = viewerService.resetViewerPassword(viewer);
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
	 * This method is used to expose the REST API as DELETE to logout viewer
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
	@RequestMapping(value = "api/1.0/logout/viewer", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String logoutViewer(@RequestHeader("Authorization") String authorization)
			throws SpotlightAuthenticationException, BusinessException {
		String operation = "logoutViewer";
		LOGGER.info("ViewerRestController :: " + operation + " :: authorization :: " + authorization);
		long startTime = System.currentTimeMillis();

		String result = "";
		if (authorization == null || !authorization.startsWith("Bearer "))
			throw new SpotlightAuthenticationException("Client is not Authorized.");

//		String username = jwtTokenUtil.getUsernameFromToken(authorization.substring(7));

		try {
			result = viewerService.logoutViewer(authorization.substring(7));
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





	@RequestMapping(value = "api/1.0/email-exist/viewer", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String emailExist(@RequestParam("email") String email)
			throws SpotlightAuthenticationException, BusinessException {
		String operation = "emailExist";
		LOGGER.info("ViewerRestController :: " + operation + " :: email :: " + email);
		long startTime = System.currentTimeMillis();
		String result = "";

		if (email == null || email == "")
			throw new ResourceNotFoundException("email can be empty !");
		try {
			result = viewerService.findByEmail(email);
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
