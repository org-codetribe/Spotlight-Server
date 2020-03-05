package com.yappyapps.spotlight.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.repository.IViewerRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IBroadcasterInfoService;
import com.yappyapps.spotlight.util.AmazonClient;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The BroadcasterInfoController class is the controller which will expose all
 * the required REST interfaces to perform CRUD on BroadcasterInfo.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/broadcaster")
public class BroadcasterInfoController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BroadcasterInfoController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "BroadcasterInfo";

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * IBroadcasterInfoService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IBroadcasterInfoService broadcasterInfoService;
	@Autowired
	private IViewerRepository viewerRepository;

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
	 * AmazonClient for AWS S3 operations
	 */
	private AmazonClient amazonClient;

	/**
	 * Constructor
	 * 
	 * @param amazonClient:
	 *            AmazonClient
	 */
	@Autowired
	BroadcasterInfoController(AmazonClient amazonClient) {
		this.amazonClient = amazonClient;
	}

	/**
	 * This method is used to expose the REST API as POST to create BroadcasterInfo.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format
	 * @param bannerUrl:
	 *            MultipartFile[]
	 * @param contentType:
	 *            "application/json"
	 * @param redirectAttributes:
	 *            RedirectAttributes
	 * @return ResponseBody: Created BroadcasterInfo in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String createBroadcasterUser(@RequestParam(value = "request") String requestBody,
			@RequestPart("bannerUrl") MultipartFile[] bannerUrl, @RequestHeader("Content-Type") String contentType,
			RedirectAttributes redirectAttributes)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createBroadcasterUser";
		LOGGER.debug("BroadcasterInfoController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType + " :: bannerUrl length:: " + bannerUrl.length);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);

		// BroadcasterInfo validations
		BroadcasterInfo broadcasterInfo = gson.fromJson(requestBody, BroadcasterInfo.class);
		utils.isEmptyOrNull(broadcasterInfo.getDisplayName(), "Display Name");
		utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getPaypalEmailId(), "Paypal Email Id");
		JSONObject reqJson = new JSONObject(requestBody);
		if (reqJson.has("commission") && reqJson.get("commission") != null
				&& !reqJson.get("commission").toString().equals("")) {
			utils.isFloat(reqJson.get("commission"), "Commission");
			utils.isValidPercentage(reqJson.get("commission"), "Commission");
			Float commission = Float.parseFloat(reqJson.get("commission").toString());
			broadcasterInfo.setCommission(commission);
		}

		utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getEmail(), "Email");
		utils.isEmailValid(broadcasterInfo.getSpotlightUser().getEmail());
		utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getName(), "Name");
//		utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getPassword(), "Password");
		// utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getUserType(), "User
		// Type");
		// utils.isUserTypeValid(broadcasterInfo.getSpotlightUser().getUserType());
		utils.isStatusValid(broadcasterInfo.getSpotlightUser().getStatus());

		utils.isLengthValid(broadcasterInfo.getShortDesc(), "Description", 255);

		if (null != bannerUrl && Arrays.asList(bannerUrl).size() > 0) {
			Arrays.asList(bannerUrl).stream().map(file -> {
				try {
					String url = this.amazonClient.uploadFile(file);
					broadcasterInfo.setBannerUrl(url);
					LOGGER.info("file URL :::: " + broadcasterInfo.getBannerUrl());
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}
				return null;
			}).collect(Collectors.toList());
		}
		try {
			result = broadcasterInfoService.createBroadcasterInfo(broadcasterInfo);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (AlreadyExistException e) {
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
	 * This method is used to expose the REST API as GET to get all BroadcasterInfo
	 * users by Genre with paging.
	 * 
	 * @param genreId:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: SpotlightUsers in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAllBroadcastersByGenre(
			@RequestParam(value = "genreId", required = false) String genreId,
			@RequestParam(value = "viewerId", required = false) String viewerId,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllBroadcastersByGenre";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: genreId :: " + genreId + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);
				if (genreId != null)
					result = broadcasterInfoService.getAllBroadcasterInfos(Integer.valueOf(genreId),
							Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
				else
					result = broadcasterInfoService.getAllBroadcasterInfos(Integer.valueOf(limit),
							Integer.valueOf(offset), direction, orderBy);
			} else {
				if (genreId != null)
					result = broadcasterInfoService.getAllBroadcasterInfos(Integer.valueOf(genreId));
				else if(viewerId != null) {
					Optional<Viewer> viewerEntity = viewerRepository.findById(Integer.valueOf(viewerId));
					if(!viewerEntity.isPresent())
						throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
					result = broadcasterInfoService.getAllBroadcasterInfoWithViewer(viewerEntity.get());
				}
				else
					result = broadcasterInfoService.getAllBroadcasterInfos();
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get all BroadcasterInfo
	 * users by Genre with paging.
	 * 
	 * @param genreId:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: SpotlightUsers in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/public/genre/name", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAllBroadcastersByGenreName(
			@RequestParam(value = "genreName", required = false) String genreName,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllBroadcastersByGenreName";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: genreName :: " + genreName + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		utils.isEmptyOrNull(genreName, "genreName");
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);
					result = broadcasterInfoService.getAllBroadcasterInfosByGenreName(genreName,
							Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
			} else {
					result = broadcasterInfoService.getAllBroadcasterInfosByGenreName(genreName);
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get all trending BroadcasterInfo
	 * users by status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param viewerId:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Broadcasters in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/public/trending", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getTrendingBroadcastersByStatus(
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "viewerId", required = false) String viewerId,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getTrendingBroadcastersByStatus";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: status :: " + status + " :: viewerId :: " + viewerId + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		Integer viewerIdVar = null;
		if(viewerId != null) {
			utils.isIntegerGreaterThanZero(viewerId, "viewerId");
			viewerIdVar = Integer.valueOf(viewerId);
		}
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);
				if (status != null)
					result = broadcasterInfoService.getTrendingBroadcasters(status, viewerIdVar, Integer.valueOf(limit),
							Integer.valueOf(offset), direction, orderBy);
				else
					result = broadcasterInfoService.getTrendingBroadcasters(viewerIdVar, Integer.valueOf(limit),
							Integer.valueOf(offset), direction, orderBy);
			} else {
				if (status != null)
					result = broadcasterInfoService.getTrendingBroadcasters(status, viewerIdVar);
				else
					result = broadcasterInfoService.getTrendingBroadcasters(viewerIdVar);
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get all BroadcasterInfo
	 * users by status with paging.
	 * 
	 * @param status:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Broadcasters in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/status/{status}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getBroadcastersByStatus(
			@PathVariable(value = "status") String status,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getBroadcastersByStatus";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		utils.isEmptyOrNull(status, "Status");
		utils.isStatusValid(status);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, BroadcasterInfo.class);
				result = broadcasterInfoService.getBroadcastersByStatus(status, Integer.valueOf(limit),
						Integer.valueOf(offset), direction, orderBy);
			} else {
				result = broadcasterInfoService.getBroadcastersByStatus(status);
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get Broadcaster User by
	 * Id.
	 * 
	 * @param spotlightUserId:
	 *            String
	 * @return ResponseBody: SpotlightUser in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/profile/id/{spotlightUserId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getBroadcasterUserProfileBySpotlightUserId(@PathVariable("spotlightUserId") String spotlightUserId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getBroadcasterUserProfileBySpotlightUserId";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: broadcasterInfoId :: " + spotlightUserId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(spotlightUserId, "spotlightUserId");
		utils.isIntegerGreaterThanZero(spotlightUserId, "spotlightUserId");
		try {
				result = broadcasterInfoService.getBroadcasterInfoBySpotlightUserId(Integer.parseInt(spotlightUserId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get Broadcaster User by
	 * Id.
	 * 
	 * @param broadcasterInfoId:
	 *            String
	 * @return ResponseBody: SpotlightUser in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{broadcasterInfoId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getBroadcasterUserById(@PathVariable("broadcasterInfoId") String broadcasterInfoId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getBroadcasterUserById";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: broadcasterInfoId :: " + broadcasterInfoId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(broadcasterInfoId, "broadcasterInfoId");
		utils.isIntegerGreaterThanZero(broadcasterInfoId, "broadcasterInfoId");
		try {
			result = broadcasterInfoService.getBroadcasterInfo(Integer.parseInt(broadcasterInfoId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get Broadcaster User by
	 * Id publicly.
	 * 
	 * @param broadcasterInfoId:
	 *            String
	 * @return ResponseBody: SpotlightUser in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/public/id/{broadcasterInfoId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getBroadcasterUserByIdPublic(
			@PathVariable("broadcasterInfoId") String broadcasterInfoId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getBroadcasterUserByIdPublic";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: broadcasterInfoId :: " + broadcasterInfoId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(broadcasterInfoId, "broadcasterInfoId");
		utils.isIntegerGreaterThanZero(broadcasterInfoId, "broadcasterInfoId");
		try {
			result = broadcasterInfoService.getBroadcasterInfo(Integer.parseInt(broadcasterInfoId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as POST to update BroadcasterInfo.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format
	 * @param bannerUrl:
	 *            MultipartFile[]
	 * @param contentType:
	 *            "application/json"
	 * @param redirectAttributes:
	 *            RedirectAttributes
	 * @return ResponseBody: Updated BroadcasterInfo in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public @ResponseBody String updateBroadcasterUser(@RequestParam(value = "request") String requestBody,
			@RequestPart("bannerUrl") MultipartFile[] bannerUrl, @RequestHeader("Content-Type") String contentType,
			RedirectAttributes redirectAttributes)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "updateBroadcasterUser";
		LOGGER.debug("BroadcasterInfoController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType + " :: bannerUrl length:: " + bannerUrl.length);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);

		// BroadcasterInfo validations
		BroadcasterInfo broadcasterInfo = gson.fromJson(requestBody, BroadcasterInfo.class);
		utils.isEmptyOrNull(broadcasterInfo.getId(), "Id");
		utils.isLengthValid(broadcasterInfo.getShortDesc(), "Description", 255);
		if(broadcasterInfo.getSpotlightUser() != null && broadcasterInfo.getSpotlightUser().getPaypalEmailId() != null) {
			utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getPaypalEmailId(), "Paypal Email Id");
		}
		broadcasterInfo.setBannerUrl(null);
		JSONObject reqJson = new JSONObject(requestBody);
		if (reqJson.has("commission") && reqJson.get("commission") != null
				&& !reqJson.get("commission").toString().equals("")) {
			utils.isFloat(reqJson.get("commission"), "Commission");
			utils.isValidPercentage(reqJson.get("commission"), "Commission");
			Float commission = Float.parseFloat(reqJson.get("commission").toString());
			broadcasterInfo.setCommission(commission);
		}

		if (null != bannerUrl && Arrays.asList(bannerUrl).size() > 0) {
			Arrays.asList(bannerUrl).stream().map(file -> {
				try {
					String url = this.amazonClient.uploadFile(file);
					broadcasterInfo.setBannerUrl(url);
					LOGGER.info("file URL :::: " + broadcasterInfo.getBannerUrl());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList());
		}
		try {
			result = broadcasterInfoService.updateBroadcasterInfo(broadcasterInfo);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (AlreadyExistException e) {
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
	 * This method is used to expose the REST API as POST to update BroadcasterInfo.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format
	 * @param bannerUrl:
	 *            MultipartFile[]
	 * @param contentType:
	 *            "application/json"
	 * @param redirectAttributes:
	 *            RedirectAttributes
	 * @return ResponseBody: Updated BroadcasterInfo in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public @ResponseBody String updateBroadcasterUserProfile(@RequestParam(value = "request") String requestBody,
			@RequestPart("bannerUrl") MultipartFile[] bannerUrl, @RequestHeader("Content-Type") String contentType,
			RedirectAttributes redirectAttributes)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "updateBroadcasterUserProfile";
		LOGGER.debug("BroadcasterInfoController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType + " :: bannerUrl length:: " + bannerUrl.length);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);

		// BroadcasterInfo validations
		BroadcasterInfo broadcasterInfo = gson.fromJson(requestBody, BroadcasterInfo.class);
		utils.isEmptyOrNull(broadcasterInfo.getId(), "Id");
		utils.isLengthValid(broadcasterInfo.getShortDesc(), "Description", 255);
		if(broadcasterInfo.getSpotlightUser() != null)
			utils.isAvailableObjectEmpty(broadcasterInfo.getSpotlightUser().getName(), "Name");
		
		if(broadcasterInfo.getSpotlightUser() != null && broadcasterInfo.getSpotlightUser().getPaypalEmailId() != null) {
			utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getPaypalEmailId(), "Paypal Email Id");
		}

		//broadcasterInfo.setBannerUrl(null);
		JSONObject reqJson = new JSONObject(requestBody);
		if (reqJson.has("commission") && reqJson.get("commission") != null
				&& !reqJson.get("commission").toString().equals("")) {
			utils.isFloat(reqJson.get("commission"), "Commission");
			utils.isValidPercentage(reqJson.get("commission"), "Commission");
			Float commission = Float.parseFloat(reqJson.get("commission").toString());
			broadcasterInfo.setCommission(commission);
		}

		if (null != bannerUrl && Arrays.asList(bannerUrl).size() > 0) {
			Arrays.asList(bannerUrl).stream().map(file -> {
				try {
					String url = this.amazonClient.uploadFile(file);
					broadcasterInfo.setBannerUrl(url);
					LOGGER.info("file URL :::: " + broadcasterInfo.getBannerUrl());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList());
		}
		try {
			result = broadcasterInfoService.updateBroadcasterInfo(broadcasterInfo);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (AlreadyExistException e) {
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

//	/**
//	 * This method is used to expose the REST API as PUT to update BroadcasterInfo Profile.
//	 * 
//	 * @param requestBody:
//	 *            Request Body in JSON Format
//	 * @param contentType:
//	 *            "application/json"
//	 *            
//	 * @return ResponseBody: Updated BroadcasterInfo in JSON format.
//	 * 
//	 * @throws InvalidParameterException
//	 *             InvalidParameterException
//	 * @throws AlreadyExistException
//	 *             AlreadyExistException
//	 * @throws BusinessException
//	 *             BusinessException
//	 */
//	@RequestMapping(value = "/profile", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
//	public @ResponseBody String updateBroadcasterUserProfile(@RequestBody String requestBody,
//			@RequestHeader("Content-Type") String contentType)
//			throws InvalidParameterException, AlreadyExistException, BusinessException {
//		String operation = "updateBroadcasterUserProfile";
//		LOGGER.debug("BroadcasterInfoController :: " + operation + " :: RequestBody :: " + requestBody
//				+ " :: contentType :: " + contentType);
//		long startTime = System.currentTimeMillis();
//		String result = "";
//		utils.isBodyJSONObject(requestBody);
//
//		// BroadcasterInfo validations
//		BroadcasterInfo broadcasterInfo = gson.fromJson(requestBody, BroadcasterInfo.class);
//		utils.isEmptyOrNull(broadcasterInfo.getId(), "Id");
//		utils.isLengthValid(broadcasterInfo.getShortDesc(), "Description", 255);
//
//		try {
//			result = broadcasterInfoService.updateBroadcasterInfo(broadcasterInfo);
//		} catch (InvalidParameterException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (AlreadyExistException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (BusinessException e) {
//			LOGGER.error(e.getMessage());
//			throw e;
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
//		} finally {
//			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
//					requestBody.length());
//		}
//
//		return result;
//	}

	/**
	 * This method is used to expose the REST API as PUT to update BroadcasterInfo
	 * status.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated BroadcasterInfo in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/status", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String updateBroadcasterUserStatus(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateBroadcasterUserStatus";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: RequestBody :: " + requestBody
				+ " :: contentType :: " + contentType);
		String result = "";
		long startTime = System.currentTimeMillis();
		utils.isBodyJSONObject(requestBody);

		// BroadcasterInfo validations
		BroadcasterInfo broadcasterInfo = gson.fromJson(requestBody, BroadcasterInfo.class);
		utils.isEmptyOrNull(broadcasterInfo.getId(), "Id");
		utils.isIntegerGreaterThanZero(broadcasterInfo.getId(), "id");

		try {
			result = broadcasterInfoService.updateBroadcasterInfo(broadcasterInfo);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as DELETE to delete
	 * BroadcasterInfo User by Id.
	 * 
	 * @param broadcasterInfoId:
	 *            String
	 * @return ResponseBody: Response in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{broadcasterInfoId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteBroadcasterUser(@PathVariable("broadcasterInfoId") String broadcasterInfoId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteBroadcasterUser";
		LOGGER.info("BroadcasterInfoController :: " + operation + " :: broadcasterInfoId :: " + broadcasterInfoId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(broadcasterInfoId, "broadcasterInfoId");
		utils.isIntegerGreaterThanZero(broadcasterInfoId, "broadcasterInfoId");
		try {
			result = broadcasterInfoService.deleteBroadcasterInfo(Integer.parseInt(broadcasterInfoId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
	 * This method is used to expose the REST API as GET to get all Categories.
	 * 
	 * @return ResponseBody: Categories in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(value = "/public/categories", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAllCategories()
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllCategories";
		LOGGER.info("BroadcasterInfoController :: " + operation);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
				result = broadcasterInfoService.getAllCategories();
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
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
