package com.yappyapps.spotlight.controller;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.helper.ViewerHelper;
import com.yappyapps.spotlight.exception.*;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.service.IBroadcasterInfoService;
import com.yappyapps.spotlight.util.*;
import org.hibernate.HibernateException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * The SpotlightUserController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on SpotlightUser.
 *
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/user/spotlight")
public class SpotlightUserController {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightUserController.class);
    @Autowired
    private ISpotlightUserRepository spotlightUserRepository;

    /**
     * Controller Name.
     */
    private static final String controller = "SpotlightUser";
    private AmazonClient amazonClient;

    public SpotlightUserController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    /**
     * MeteringService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private MeteringService meteringService;

    /**
     * ISpotlightUserService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ISpotlightUserService spotlightUserService;
    @Autowired
    private IBroadcasterInfoService broadcasterInfoService;


    /**
     * Gson dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Gson gson;
    @Autowired
    private IViewerRepository viewerRepository;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ViewerHelper viewerHelper;
    /**
     * IViewerService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */

    /**
     * This method is used to expose the REST API as POST to create SpotlightUser.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Created SpotlightUser in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String createSpotlightUser(@RequestParam(value = "request") String requestBody,
                               @RequestHeader("Content-Type") String contentType, @RequestPart(value = "bannerUrl",required = false) MultipartFile[] bannerUrl,
                               @RequestPart(value = "image",required = false) MultipartFile[] image,
                               @RequestHeader(value = "Authorization", required = false) String authorization)
            throws InvalidParameterException, AlreadyExistException, BusinessException {
        String operation = "createSpotlightUser";
        LOGGER.debug("SpotlightUserController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isBodyJSONObject(requestBody);
         BroadcasterInfo broadcasterInfo = gson.fromJson(requestBody, BroadcasterInfo.class);

       // utils.isEmptyOrNull(spotlightUser.getEmail(), "Email");
       // utils.isEmailValid(spotlightUser.getEmail());
        utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getName(), "Name");
        utils.isEmptyOrNull(broadcasterInfo.getSpotlightUser().getUserType(), "User Type");
        utils.isEmptyOrNull(broadcasterInfo.getShortDesc(), "shortDesc");
        utils.isEmptyOrNull(broadcasterInfo.getBiography(), "biography");
        utils.isUserTypeValid(broadcasterInfo.getSpotlightUser().getUserType());
        utils.isStatusValid(broadcasterInfo.getSpotlightUser().getStatus());
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
        }else{
            broadcasterInfo.setBannerUrl("NO_IMAGE_FOUND");
        }


        if (null != image && Arrays.asList(image).size() > 0) {
            Arrays.asList(image).stream().map(file -> {
                try {
                    String url_ = this.amazonClient.uploadFile(file);
                    broadcasterInfo.getSpotlightUser().setProfileUrl(url_);
                    LOGGER.info("file URL :::: " + broadcasterInfo.getSpotlightUser().getProfileUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(e.getMessage());
                }
                return null;
            }).collect(Collectors.toList());
        }else {
            broadcasterInfo.getSpotlightUser().setProfileUrl("NO_IMAGE_FOUND");
        }

        try {
            Viewer viewerEntity = null;
            if (broadcasterInfo.getSpotlightUser().getUserType().equalsIgnoreCase("BROADCASTER")) {
                if (authorization == null || !authorization.startsWith("Bearer "))
                    throw new SpotlightAuthenticationException("Client is not Authorized.");
                 String username = jwtTokenUtil.getUsernameFromToken(authorization.substring(7));


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


                String entityPassword = viewerEntity.getPassword();
                broadcasterInfo.getSpotlightUser().setPassword(entityPassword);
                broadcasterInfo.getSpotlightUser().setEmail(viewerEntity.getEmail());
                broadcasterInfo.getSpotlightUser().setUsername(viewerEntity.getUsername());
                broadcasterInfo.getSpotlightUser().setToken(authorization);
                broadcasterInfo.setDisplayName(broadcasterInfo.getSpotlightUser().getName());


            }
            //result = spotlightUserService.createSpotlightUser(broadcasterInfo.getSpotlightUser());
            //SpotlightUser  byEmail = spotlightUserRepository.findByEmail(viewerEntity.getEmail());
            //byEmail.setEmail(null);
           // broadcasterInfo.setSpotlightUser(byEmail);
             //BroadcasterInfo broadcasterInfo1 = gson.fromJson(result, BroadcasterInfo.class);
           //  broadcasterInfo1.setDisplayName(b);
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
     * This method is used to expose the REST API as GET to get all SpotlightUsers
     * with paging.
     *
     * @param limit:     String
     * @param offset:    String
     * @param direction: String
     * @param orderBy:   String
     * @return ResponseBody: SpotlightUsers in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getAllSpotlightUsers(@RequestParam(value = "limit", required = false) String limit,
                                @RequestParam(value = "offset", required = false) String offset,
                                @RequestParam(value = "direction", required = false) String direction,
                                @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAllSpotlightUsers";
        LOGGER.info("SpotlightUserController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
                + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = null;

        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, SpotlightUser.class);
                result = spotlightUserService.getAllSpotlightUsers(Integer.valueOf(limit), Integer.valueOf(offset),
                        direction, orderBy);
            } else {
                result = spotlightUserService.getAllSpotlightUsers();
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
     * This method is used to expose the REST API as GET to get SpotlightUser by Id.
     *
     * @param userId: String
     * @return ResponseBody: SpotlightUser in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{userId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getSpotlightUserById(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getSpotlightUserById";
        LOGGER.info("SpotlightUserController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(userId, "userId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = spotlightUserService.getSpotlightUser(Integer.parseInt(userId));
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
     * This method is used to expose the REST API as GET to get SpotlightUser by Id.
     *
     * @param userId: String
     * @return ResponseBody: SpotlightUser in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/profile/id/{userId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getSpotlightUserProfileById(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getSpotlightUserById";
        LOGGER.info("SpotlightUserController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();

        String result = "";
        utils.isEmptyOrNull(userId, "userId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = spotlightUserService.getSpotlightUser(Integer.parseInt(userId));
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
     * This method is used to expose the REST API as GET to get SpotlightUsers by
     * type by Id.
     *
     * @param userType:  String
     * @param limit:     String
     * @param offset:    String
     * @param direction: String
     * @param orderBy:   String
     * @return ResponseBody: SpotlightUsers in JSON format
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/type/{userType}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getSpotlightUsersByType(@PathVariable("userType") String userType,
                                   @RequestParam(value = "limit", required = false) String limit,
                                   @RequestParam(value = "offset", required = false) String offset,
                                   @RequestParam(value = "direction", required = false) String direction,
                                   @RequestParam(value = "orderBy", required = false) String orderBy)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getSpotlightUsersByType";
        LOGGER.info("SpotlightUserController :: " + operation + " :: userType :: " + userType + " :: limit :: " + limit
                + " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isEmptyOrNull(userType, "User Type");
        utils.isUserTypeValid(userType);
        try {
            if (offset != null && limit != null) {
                utils.isInteger(offset, "offset");
                utils.isInteger(limit, "limit");
                utils.isOrderByDirectionValid(direction);
                utils.isOrderByPropertyValid(orderBy, SpotlightUser.class);
                result = spotlightUserService.getSpotlightUsersByType(userType, Integer.valueOf(limit),
                        Integer.valueOf(offset), direction, orderBy);
            } else {
                result = spotlightUserService.getSpotlightUsersByType(userType);
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
     * This method is used to expose the REST API as PUT to change SpotlightUser
     * password.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String changeSpotlightUserPassword(@RequestBody String requestBody,
                                       @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "changeSpotlightUserPassword";
        LOGGER.info("SpotlightUserController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
        utils.isEmptyOrNull(spotlightUser.getUsername(), "User Name");
        utils.isEmptyOrNull(spotlightUser.getPassword(), "Password");
        JSONObject requestObj = new JSONObject(requestBody);
        utils.isEmptyOrNull(requestObj.has("oldPassword") ? requestObj.getString("oldPassword") : "", "Old Password");

        try {
            String oldPassword = new JSONObject(requestBody).getString("oldPassword");

            if (!spotlightUserService.verifyOldPassword(spotlightUser, oldPassword)) {
                throw new InvalidParameterException(IConstants.OLD_PASSWORD_DONOT_MATCH);
            }
            result = spotlightUserService.changeSpotlightUserPassword(spotlightUser);
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
     * This method is used to expose the REST API as PUT to update SpotlightUser.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateSpotlightUser(@RequestBody String requestBody,
                               @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateSpotlightUser";
        LOGGER.info("SpotlightUserController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
        utils.isEmptyOrNull(spotlightUser.getId(), "id");
        utils.isIntegerGreaterThanZero(spotlightUser.getId(), "id");
        // utils.isEmptyOrNull(spotlightUser.getEmail(), "Email");
        try {
            result = spotlightUserService.updateSpotlightUser(spotlightUser);
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
     * This method is used to expose the REST API as PUT to update SpotlightUser Profile.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/profile", method = RequestMethod.PUT, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateSpotlightUserProfile(@RequestParam("request") String requestBody,
                                      @RequestHeader("Content-Type") String contentType,@RequestPart(value = "profilePicture", required = false) MultipartFile[] image,
                                      @RequestPart(value = "bannerUrl", required = false) MultipartFile[] bannerUrl,
                                      @RequestParam(value = "shortDesc",required = false)String shortDesc,
                                      @RequestParam(value = "biography",required = false)String biography)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateSpotlightUserProfile";
        LOGGER.info("SpotlightUserController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
        utils.isEmptyOrNull(spotlightUser.getId(), "id");
        utils.isIntegerGreaterThanZero(spotlightUser.getId(), "id");
       // utils.isAvailableObjectEmpty(spotlightUser.getName(), "Name");
        // utils.isEmptyOrNull(spotlightUser.getEmail(), "Email");
        try {

            if (null != image && Arrays.asList(image).size() > 0) {
                Arrays.asList(image).stream().map(file -> {
                    try {
                        LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
                        String url = this.amazonClient.uploadFile(file);
                         spotlightUser.setProfileUrl(url);
                        LOGGER.info("file URL :::: " + spotlightUser.getProfileUrl());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
            }
            String  broadcasterInfo = broadcasterInfoService.getBroadcasterInfoBySpotlightUserId(spotlightUser.getId());
            Result response =  gson.fromJson(broadcasterInfo,Result.class);

            BroadcasterInfo broadcasterInfo1 = response.getResponse().getBroadcaster();
            if(shortDesc !=null && shortDesc != "" && shortDesc.length() > 0){
                broadcasterInfo1.setShortDesc(shortDesc);
            }
            if(biography !=null && biography != "" && biography.length() > 0){
                broadcasterInfo1.setBiography(biography);
            }
            if (null != bannerUrl && Arrays.asList(bannerUrl).size() > 0) {
                Arrays.asList(bannerUrl).stream().map(file -> {
                    try {
                        LOGGER.info("File Name :::::::::::::::::::::::: " + file.getName());
                        String url = this.amazonClient.uploadFile(file);
                        broadcasterInfo1.setBannerUrl(url);
                       LOGGER.info("file URL :::: " + broadcasterInfo1.getBannerUrl());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
            }
            spotlightUserService.updateSpotlightUser(spotlightUser);
            String  spotlightUserResult = spotlightUserService.getSpotlightUser(spotlightUser.getId());
            Result spotlightUser_ = gson.fromJson(spotlightUserResult,Result.class);
            broadcasterInfo1.setSpotlightUser(spotlightUser_.getResponse().getUser());

           result =  broadcasterInfoService.updateBroadcasterInfo(broadcasterInfo1);
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
     * This method is used to expose the REST API as PUT to update SpotlightUser
     * status.
     *
     * @param requestBody: Request Body in JSON Format.
     * @param contentType: "application/json"
     * @return ResponseBody: Updated SpotlightUser in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/status", method = RequestMethod.PUT, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String updateSpotlightUserStatus(@RequestBody String requestBody,
                                     @RequestHeader("Content-Type") String contentType)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "updateSpotlightUserStatus";
        LOGGER.info("SpotlightUserController :: " + operation + " :: RequestBody :: " + requestBody
                + " :: contentType :: " + contentType);
        String result = "";
        long startTime = System.currentTimeMillis();
        utils.isBodyJSONObject(requestBody);
        SpotlightUser spotlightUser = gson.fromJson(requestBody, SpotlightUser.class);
        utils.isEmptyOrNull(spotlightUser.getId(), "id");
        utils.isIntegerGreaterThanZero(spotlightUser.getId(), "id");
        try {
            result = spotlightUserService.updateSpotlightUser(spotlightUser);
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
     * This method is used to expose the REST API as DELETE to delete SpotlightUser
     * by Id.
     *
     * @param userId: String
     * @return ResponseBody: Response in JSON format.
     * @throws InvalidParameterException InvalidParameterException
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     */
    @RequestMapping(value = "/id/{userId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String deleteSpotlightUser(@PathVariable("userId") String userId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "deleteSpotlightUser";
        LOGGER.info("SpotlightUserController :: " + operation + " :: userId :: " + userId);
        long startTime = System.currentTimeMillis();
        String result = "";
        utils.isEmptyOrNull(userId, "UserId");
        utils.isIntegerGreaterThanZero(userId, "userId");
        try {
            result = spotlightUserService.deleteSpotlightUser(Integer.parseInt(userId));
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


 class Response {
    @SerializedName("broadcaster")
    @Expose
    private BroadcasterInfo broadcaster;

     @SerializedName("user")
     @Expose
     private SpotlightUser user;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public BroadcasterInfo getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(BroadcasterInfo broadcaster) {
        this.broadcaster = broadcaster;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

     public SpotlightUser getUser() {
         return user;
     }

     public void setUser(SpotlightUser user) {
         this.user = user;
     }
 }

class Result {

    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}