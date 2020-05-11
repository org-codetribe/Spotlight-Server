package com.yappyapps.spotlight.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.DefaultConfiguration;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.domain.SpotlightCommission;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.helper.BroadcasterInfoHelper;
import com.yappyapps.spotlight.domain.helper.SpotlightCommissionHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.IDefaultConfigurationRepository;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.repository.IEventTypeRepository;
import com.yappyapps.spotlight.repository.IGenreRepository;
import com.yappyapps.spotlight.repository.ISpotlightCommissionRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.service.IBroadcasterInfoService;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.util.AmazonClient;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The BroadcasterInfoService class is the implementation of
 * IBroadcasterInfoService
 *
 * <h1>@Service</h1> denotes that it is a service class *
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class BroadcasterInfoService implements IBroadcasterInfoService {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcasterInfoService.class);

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
     * ISpotlightCommissionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ISpotlightCommissionRepository spotlightCommissionRepository;

    /**
     * IDefaultConfigurationRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IDefaultConfigurationRepository defaultConfigurationRepository;

    /**
     * IGenreRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IGenreRepository genreRepository;

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
     * IEventTypeRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventTypeRepository eventTypeRepository;

    /**
     * IEmailNotificationService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEmailNotificationService emailNotificationService;

    /**
     * BroadcasterInfoHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private BroadcasterInfoHelper broadcasterInfoHelper;

    /**
     * SpotlightCommissionHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private SpotlightCommissionHelper spotlightCommissionHelper;

    /**
     * AmazonClient dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private AmazonClient amazonClient;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    /**
     * This method is used to create the BroadcasterInfo User.
     *
     * @param broadcasterInfoReqObj: BroadcasterInfo
     * @return String: Response
     * @throws AlreadyExistException AlreadyExistException
     * @throws BusinessException     BusinessException
     * @throws Exception             Exception
     */
    @Override
    public String createBroadcasterInfo(BroadcasterInfo broadcasterInfoReqObj)
            throws AlreadyExistException, BusinessException, Exception {
        String result = null;
        boolean generatePasswordFlag = false;
        String generatedPassword = "";
        if (broadcasterInfoReqObj.getSpotlightUser().getPassword() == null || broadcasterInfoReqObj.getSpotlightUser().getPassword().isEmpty()) {
            generatedPassword = Utils.generateRandomPassword(10);
            if (broadcasterInfoReqObj.getSpotlightUser().getPassword() == null
                    || broadcasterInfoReqObj.getSpotlightUser().getPassword().trim().equalsIgnoreCase("")) {
                generatePasswordFlag = true;
            }
            if (generatePasswordFlag) {
                broadcasterInfoReqObj.getSpotlightUser().setPassword(generatedPassword);
                LOGGER.info("generatedPassword    ::::: " + generatedPassword);
            }
        }

        if (spotlightUserRepository.findByEmail(broadcasterInfoReqObj.getSpotlightUser().getEmail()) != null) {
            this.amazonClient.deleteFileFromS3Bucket(broadcasterInfoReqObj.getBannerUrl());
            throw new AlreadyExistException("Email already exists.");
        }
        Float commission = null;
        if (broadcasterInfoReqObj.getCommission() != null) {
            LOGGER.info("broadcasterInfo.getCommission() :::::::::::::::::::::::::::::::::::::::::: "
                    + broadcasterInfoReqObj.getCommission());
            commission = broadcasterInfoReqObj.getCommission();
        } else {
            DefaultConfiguration defaultConfiguration = defaultConfigurationRepository
                    .findByName("SPOTLIGHT_DEFAULT_COMMISSION");
            commission = Float.parseFloat(defaultConfiguration.getValue().toString());
            broadcasterInfoReqObj.setCommission(commission);
        }

        try {
            broadcasterInfoReqObj = broadcasterInfoHelper.populateBroadcasterInfo(broadcasterInfoReqObj);
            broadcasterInfoReqObj = broadcasterInfoRepository.save(broadcasterInfoReqObj);
            if (commission != null) {

                SpotlightCommission spotlightCommissionReqObj = new SpotlightCommission();
                spotlightCommissionReqObj.setBroadcasterInfo(broadcasterInfoReqObj);
                spotlightCommissionReqObj.setPercentage(commission);
                SpotlightCommission spotlightCommission = spotlightCommissionHelper
                        .populateSpotlightCommission(spotlightCommissionReqObj);
                spotlightCommissionRepository.save(spotlightCommission);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            this.amazonClient.deleteFileFromS3Bucket(broadcasterInfoReqObj.getBannerUrl());
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            this.amazonClient.deleteFileFromS3Bucket(broadcasterInfoReqObj.getBannerUrl());
            throw new Exception(sqlException.getMessage());
        }

        if (generatePasswordFlag) {
            try {
                broadcasterInfoReqObj.getSpotlightUser().setPassword(generatedPassword);
                emailNotificationService.sendMimeMessage(broadcasterInfoReqObj.getSpotlightUser(),
                        "Spotlight Account Credentials");
                LOGGER.info("email sent for createBroadcasterInfo ::::: 	");
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTER, broadcasterInfoHelper.buildResponseObject(broadcasterInfoReqObj, null, false));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all BroadcasterInfo User.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllBroadcasterInfos() throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        try {
            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findAll();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }


    @Override
    public String getAllBroadcasterInfoWithViewer(Viewer viewer) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        try {
            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findAll();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, viewer));

        result = utils.constructSucessJSON(jObj);

        return result;

    }


    /**
     * This method is used to get all BroadcasterInfo Users by Genre.
     *
     * @param genreId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllBroadcasterInfos(Integer genreId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        try {
            Optional<Genre> genreEntity = genreRepository.findById(genreId);
            if (!genreEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findByGenre(genreEntity.get());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all BroadcasterInfo Users by Genre Name.
     *
     * @param genreName: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllBroadcasterInfosByGenreName(String genreName)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        try {
            Genre genreEntity = genreRepository.findByName(genreName);
            if (genreEntity == null)
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findByGenre(genreEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all BroadcasterInfo Users with paging and orderBy.
     *
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllBroadcasterInfos(Integer limit, Integer offset, String direction, String orderBy)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        int pageNum = offset / limit;

        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<BroadcasterInfo> broadcasterInfos = broadcasterInfoRepository.findAll(pageableRequest);
            totalCount = broadcasterInfos.getTotalElements();
            List<BroadcasterInfo> broadcasterInfoEntities = broadcasterInfos.getContent();
            for (BroadcasterInfo broadcasterInfoEntity : broadcasterInfoEntities) {
                BroadcasterInfo broadcasterInfoDto = new BroadcasterInfo();
                BeanUtils.copyProperties(broadcasterInfoEntity, broadcasterInfoDto);
                broadcasterInfoList.add(broadcasterInfoDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all BroadcasterInfo Users by Genre with paging and
     * orderBy.
     *
     * @param genreId:   Integer
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllBroadcasterInfos(Integer genreId, Integer limit, Integer offset, String direction,
                                         String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        int pageNum = offset / limit;

        try {
            Optional<Genre> genreEntity = genreRepository.findById(genreId);
            if (!genreEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<BroadcasterInfo> broadcasterInfos = broadcasterInfoRepository.findByGenre(genreEntity.get(),
                    pageableRequest);
            totalCount = broadcasterInfos.getTotalElements();
            List<BroadcasterInfo> broadcasterInfoEntities = broadcasterInfos.getContent();
            for (BroadcasterInfo broadcasterInfoEntity : broadcasterInfoEntities) {
                BroadcasterInfo broadcasterInfoDto = new BroadcasterInfo();
                BeanUtils.copyProperties(broadcasterInfoEntity, broadcasterInfoDto);
                broadcasterInfoList.add(broadcasterInfoDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all BroadcasterInfo Users by Genre Name with paging and
     * orderBy.
     *
     * @param genreName: String
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllBroadcasterInfosByGenreName(String genreName, Integer limit, Integer offset, String direction,
                                                    String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        int pageNum = offset / limit;

        try {
            Genre genreEntity = genreRepository.findByName(genreName);
            if (genreEntity == null)
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<BroadcasterInfo> broadcasterInfos = broadcasterInfoRepository.findByGenre(genreEntity,
                    pageableRequest);
            totalCount = broadcasterInfos.getTotalElements();
            List<BroadcasterInfo> broadcasterInfoEntities = broadcasterInfos.getContent();
            for (BroadcasterInfo broadcasterInfoEntity : broadcasterInfoEntities) {
                BroadcasterInfo broadcasterInfoDto = new BroadcasterInfo();
                BeanUtils.copyProperties(broadcasterInfoEntity, broadcasterInfoDto);
                broadcasterInfoList.add(broadcasterInfoDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all trending BroadcasterInfo Users.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getTrendingBroadcasters(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findByIsTrending(true);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all trending BroadcasterInfo Users by status.
     *
     * @param status: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getTrendingBroadcasters(String status, Integer viewerId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findByStatusAndIsTrending(status,
                    true);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all trending BroadcasterInfo Users with paging and
     * orderBy.
     *
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getTrendingBroadcasters(Integer viewerId, Integer limit, Integer offset, String direction, String orderBy)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        int pageNum = offset / limit;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<BroadcasterInfo> broadcasterInfos = broadcasterInfoRepository.findByIsTrending(true, pageableRequest);
            totalCount = broadcasterInfos.getTotalElements();
            List<BroadcasterInfo> broadcasterInfoEntities = broadcasterInfos.getContent();
            for (BroadcasterInfo broadcasterInfoEntity : broadcasterInfoEntities) {
                BroadcasterInfo broadcasterInfoDto = new BroadcasterInfo();
                BeanUtils.copyProperties(broadcasterInfoEntity, broadcasterInfoDto);
                broadcasterInfoList.add(broadcasterInfoDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all trending BroadcasterInfo Users by status with
     * paging and orderBy.
     *
     * @param status:    String
     * @param viewerId:  Integer
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getTrendingBroadcasters(String status, Integer viewerId, Integer limit, Integer offset, String direction,
                                          String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        int pageNum = offset / limit;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<BroadcasterInfo> broadcasterInfos = broadcasterInfoRepository.findByStatusAndIsTrending(status, true,
                    pageableRequest);
            totalCount = broadcasterInfos.getTotalElements();
            List<BroadcasterInfo> broadcasterInfoEntities = broadcasterInfos.getContent();
            for (BroadcasterInfo broadcasterInfoEntity : broadcasterInfoEntities) {
                BroadcasterInfo broadcasterInfoDto = new BroadcasterInfo();
                BeanUtils.copyProperties(broadcasterInfoEntity, broadcasterInfoDto);
                broadcasterInfoList.add(broadcasterInfoDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all BroadcasterInfo Users by status.
     *
     * @param status: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getBroadcastersByStatus(String status)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        try {
            broadcasterInfoList = (List<BroadcasterInfo>) broadcasterInfoRepository.findByStatus(status);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all BroadcasterInfo Users by status with
     * paging and orderBy.
     *
     * @param status:    String
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getBroadcastersByStatus(String status, Integer limit, Integer offset, String direction,
                                          String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<BroadcasterInfo>();
        int pageNum = offset / limit;

        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<BroadcasterInfo> broadcasterInfos = broadcasterInfoRepository.findByStatus(status,
                    pageableRequest);
            totalCount = broadcasterInfos.getTotalElements();
            List<BroadcasterInfo> broadcasterInfoEntities = broadcasterInfos.getContent();
            for (BroadcasterInfo broadcasterInfoEntity : broadcasterInfoEntities) {
                BroadcasterInfo broadcasterInfoDto = new BroadcasterInfo();
                BeanUtils.copyProperties(broadcasterInfoEntity, broadcasterInfoDto);
                broadcasterInfoList.add(broadcasterInfoDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (broadcasterInfoList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;


    }

    /**
     * This method is used to get BroadcasterInfo User by broadcasterInfoId.
     *
     * @param broadcasterInfoId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getBroadcasterInfo(Integer broadcasterInfoId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        Optional<BroadcasterInfo> broadcasterInfo = null;
        try {
            broadcasterInfo = broadcasterInfoRepository.findById(broadcasterInfoId);
            if (!broadcasterInfo.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTER, broadcasterInfoHelper.buildResponseObject(broadcasterInfo.get(), null, true));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get BroadcasterInfo User by spotlightUserId.
     *
     * @param spotlightUserId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getBroadcasterInfoBySpotlightUserId(Integer spotlightUserId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Viewer viewer = null;

        BroadcasterInfo broadcasterInfo = null;
        try {
            Optional<SpotlightUser> spotlightUserEntity = spotlightUserRepository.findById(spotlightUserId);
            if (!spotlightUserEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            if (spotlightUserEntity.isPresent()) {
                viewer = viewerRepository.findByEmail(spotlightUserEntity.get().getEmail());
            }

            broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUserEntity.get());
            if (broadcasterInfo == null)
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTER, broadcasterInfoHelper.buildResponseObject(broadcasterInfo, viewer != null ? viewer : null, true));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to update the BroadcasterInfo User.
     *
     * @param broadcasterInfoReqObj: BroadcasterInfo
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String updateBroadcasterInfo(BroadcasterInfo broadcasterInfoReqObj)
            throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;

        // String previousUrl = broadcasterInfo.getBannerUrl(); // For Deletion of
        // previous files
        //String previousUrl = "";
        Optional<BroadcasterInfo> broadcasterInfoEntity = null;

        try {
            broadcasterInfoEntity = broadcasterInfoRepository.findById(broadcasterInfoReqObj.getId());
            if (!broadcasterInfoEntity.isPresent()) {
				/*if (broadcasterInfoReqObj.getBannerUrl() != null) {
					this.amazonClient.deleteFileFromS3Bucket(broadcasterInfoReqObj.getBannerUrl());
					LOGGER.info("Broadcaster is not present. deleting new uploaded file ::::::::: "
							+ broadcasterInfoReqObj.getBannerUrl());
				}*/
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            } /*else if (broadcasterInfoReqObj.getBannerUrl() != null) {
				previousUrl = broadcasterInfoEntity.get().getBannerUrl();
			}*/

            if (broadcasterInfoReqObj.getStatus() != null && broadcasterInfoReqObj.getStatus().equalsIgnoreCase("InActive")) {
                List<Event> eventList = eventRepository.findByBroadcasterInfoAndStatusAndEventUtcDatetimeGreaterThanOrderByEventUtcDatetimeDesc(broadcasterInfoEntity.get(), "Active", new Timestamp(System.currentTimeMillis()));

                if (!eventList.isEmpty()) {
                    throw new InvalidParameterException("We cannot deactivate the broadcaster as there are upcoming events for the broadcaster.");
                }

            }

            // if(broadcasterInfo.getSpotlightUser().getPhone() != null) {
            // if((spotlightUserRepository.findByPhone(broadcasterInfo.getSpotlightUser().getPhone())
            // != null)) {
            // throw new AlreadyExistException("User with same Phone already exists.");
            // }
            // }

            Float commission = null;
            if (broadcasterInfoReqObj.getCommission() != null) {
                LOGGER.info("broadcasterInfo.getCommission() :::::::::::::::::::::::::::::::::::::::::: "
                        + broadcasterInfoReqObj.getCommission());
                commission = broadcasterInfoReqObj.getCommission();
            } /*
             * else { DefaultConfiguration defaultConfiguration =
             * defaultConfigurationRepository.findByName("SPOTLIGHT_DEFAULT_COMMISSION");
             * commission = Float.parseFloat(defaultConfiguration.getValue().toString());
             * broadcasterInfo.setCommission(commission); }
             */

            broadcasterInfoHelper.populateBroadcasterInfo(broadcasterInfoReqObj, broadcasterInfoEntity.get());
            broadcasterInfoRepository.save(broadcasterInfoEntity.get());

            if (commission != null) {
                SpotlightCommission spotlightCommissionEntity = spotlightCommissionRepository
                        .findByBroadcasterInfoAndEvent(broadcasterInfoEntity.get(), null);


                SpotlightCommission spotlightCommissionReqObj = new SpotlightCommission();
                spotlightCommissionReqObj.setBroadcasterInfo(broadcasterInfoEntity.get());
                spotlightCommissionReqObj.setPercentage(commission);
                SpotlightCommission spotlightCommission = null;
                if (spotlightCommissionEntity != null)
                    spotlightCommission = spotlightCommissionHelper
                            .populateSpotlightCommission(spotlightCommissionReqObj, spotlightCommissionEntity);
                else
                    spotlightCommission = spotlightCommissionHelper
                            .populateSpotlightCommission(spotlightCommissionReqObj);
                spotlightCommissionRepository.save(spotlightCommission);
            }
			/*if (!previousUrl.equals("")) {
				this.amazonClient.deleteFileFromS3Bucket(previousUrl);
				LOGGER.info("Finally. deleting previous broadcaster banner file ::::::::: " + previousUrl);
			}*/
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            if (broadcasterInfoEntity.get().getBannerUrl() != null) {
                this.amazonClient.deleteFileFromS3Bucket(broadcasterInfoEntity.get().getBannerUrl());
                LOGGER.info("ConstraintViolationException. deleting file ::::::::: "
                        + broadcasterInfoEntity.get().getBannerUrl());
            }
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        } catch (HibernateException | JpaSystemException sqlException) {
            if (broadcasterInfoEntity.get().getBannerUrl() != null) {
                this.amazonClient.deleteFileFromS3Bucket(broadcasterInfoEntity.get().getBannerUrl());
                LOGGER.info(
                        "HibernateException. deleting file ::::::::: " + broadcasterInfoEntity.get().getBannerUrl());
            }
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTER, broadcasterInfoHelper.buildResponseObject(broadcasterInfoEntity.get(), null, false));
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to delete the BroadcasterInfo by id.
     *
     * @param broadcasterInfoId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    @Transactional
    public String deleteBroadcasterInfo(Integer broadcasterInfoId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        try {
            Optional<BroadcasterInfo> broadcasterInfo = broadcasterInfoRepository.findById(broadcasterInfoId);
            if (!broadcasterInfo.isPresent())
                throw new ResourceNotFoundException("Broadcaster(s) do not exist.");

            List<Event> eventList = eventRepository.findByBroadcasterInfo(broadcasterInfo.get());

            if (!eventList.isEmpty()) {
                throw new InvalidParameterException("We cannot delete the broadcaster as there is associated data with the broadcaster. Please deactivate it.");
            }

            SpotlightCommission spotlightCommission = spotlightCommissionRepository
                    .findByBroadcasterInfoAndEvent(broadcasterInfo.get(), null);
            if (spotlightCommission != null) {
                spotlightCommissionRepository.delete(spotlightCommission);
            }

            if (broadcasterInfo != null) {
                broadcasterInfoRepository.delete(broadcasterInfo.get());
                this.amazonClient.deleteFileFromS3Bucket(broadcasterInfo.get().getBannerUrl());
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new InvalidParameterException("We cannot delete the broadcaster as there is associated data with the broadcaster. Please deactivate it.");
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
        JSONObject jObj = new JSONObject();

        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get all Categories.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String getAllCategories() throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
        String result = null;

        List<Genre> genreList = null;
        List<EventType> eventTypeList = null;
        JSONArray categoriesArr = new JSONArray();
        try {
            genreList = genreRepository.findByIsCategory(true);
            eventTypeList = eventTypeRepository.findByIsCategory(true);
            Set<String> categoriesSet = new HashSet<String>();
            for (Genre genre : genreList) {
                categoriesSet.add(genre.getName());
            }
            for (EventType eventType : eventTypeList) {
                categoriesSet.add(eventType.getName());
            }

            for (String categoryName : categoriesSet) {
                categoriesArr.put(categoryName);
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.CATEGORIES, categoriesArr);

        result = utils.constructSucessJSON(jObj);

        return result;
    }

}
