package com.yappyapps.spotlight.service.impl;

import java.sql.Timestamp;
import java.util.*;

import com.yappyapps.spotlight.domain.*;
import com.yappyapps.spotlight.repository.*;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.helper.BroadcasterInfoHelper;
import com.yappyapps.spotlight.domain.helper.EventHelper;
import com.yappyapps.spotlight.domain.helper.FavoriteHelper;
import com.yappyapps.spotlight.domain.helper.ViewerHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.service.IViewerService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The ViewerService class is the implementation of IViewerService
 *
 * <h1>@Service</h1> denotes that it is a service class
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

    @Autowired
    IOrderRepository orderRepository;

    @Autowired
    private ISpotlightUserRepository spotlightUserRepository;

    /**
     * IFavoriteRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IFavoriteRepository favoriteRepository;


    @Autowired
    private IWalletRepository walletRepository;


    /**
     * IViewerEventRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerEventRepository viewerEventRepository;

    /**
     * IEventRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IEventTypeRepository eventTypeRepository;

    /**
     * IBroadcasterInfoRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IBroadcasterInfoRepository broadcasterInfoRepository;

    /**
     * IViewerSessionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerSessionRepository viewerSessionRepository;

    /**
     * BroadcasterInfoHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private BroadcasterInfoHelper broadcasterInfoHelper;

    /**
     * ViewerHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ViewerHelper viewerHelper;

    /**
     * FavoriteHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private FavoriteHelper favoriteHelper;

    /**
     * EventHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private EventHelper eventHelper;

    /**
     * IEmailNotificationService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEmailNotificationService emailNotificationService;

    /**
     * PasswordEncoder dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    /**
     * This method is used to create the Viewer.
     *
     * @param viewerReqObj: Viewer
     * @return String: Response
     * @throws AlreadyExistException AlreadyExistException
     * @throws BusinessException     BusinessException
     * @throws Exception             Exception
     */
    @Override
    public String createViewer(Viewer viewerReqObj) throws AlreadyExistException, BusinessException, Exception {
        String result = null;

        boolean generatePasswordFlag = false;

        String generatedPassword = Utils.generateRandomPassword(10);
        if (viewerReqObj.getPassword() == null || viewerReqObj.getPassword().trim().equalsIgnoreCase("")) {
            generatePasswordFlag = true;
        }
        if (generatePasswordFlag) {
            viewerReqObj.setPassword(generatedPassword);
            LOGGER.info("generatedPassword    ::::: " + generatedPassword);
        }

        if (viewerRepository.findByEmail(viewerReqObj.getEmail()) != null) {
            throw new AlreadyExistException("Email already exists.");
        }

        if (viewerRepository.findByChatName(viewerReqObj.getChatName()) != null) {
            throw new AlreadyExistException("Chat Name already exists.");
        }

        try {
            viewerReqObj = viewerHelper.populateViewer(viewerReqObj);
            viewerReqObj = viewerRepository.save(viewerReqObj);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (generatePasswordFlag) {
            try {
                viewerReqObj.setPassword(generatedPassword);
                emailNotificationService.sendMimeMessage(viewerReqObj, IConstants.EMAIL_SUBJECT_ACCOUNT_CREDENTIALS);
                LOGGER.info("email sent for createViewer ::::: ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObject(viewerReqObj));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to mark Broadcaster as Favorite.
     *
     * @param favoriteReqObj: Favorite.
     * @return String: Response
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String manageFavoriteBroadcaster(Favorite favoriteReqObj, Boolean favoriteFlag) throws InvalidParameterException, AlreadyExistException, BusinessException, Exception {
        String result = null;


        try {
            favoriteReqObj = favoriteHelper.populateFavorite(favoriteReqObj);
//			Optional<Event> eventEntity = eventRepository.findById(favoriteReqObj.getEvent().getId());
//			if(!eventEntity.isPresent())
//				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(favoriteReqObj.getBroadcasterInfo().getId());
            if (!broadcasterInfoEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Optional<Viewer> viewerEntity = viewerRepository.findById(favoriteReqObj.getViewer().getId());
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            if (favoriteFlag) {
                if (favoriteRepository.findByBroadcasterInfoAndEventAndViewer(favoriteReqObj.getBroadcasterInfo(), favoriteReqObj.getEvent(), favoriteReqObj.getViewer()) != null)
                    throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
                favoriteReqObj = favoriteRepository.save(favoriteReqObj);
            } else {
                Favorite favoriteEntity = favoriteRepository.findByBroadcasterInfoAndEventAndViewer(favoriteReqObj.getBroadcasterInfo(), favoriteReqObj.getEvent(), favoriteReqObj.getViewer());
                if (favoriteEntity == null)
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
                favoriteRepository.delete(favoriteEntity);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to mark Event as Favorite.
     *
     * @param favorite: Favorite.
     * @return String: Response
     * @throws InvalidParameterException InvalidParameterException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String manageFavoriteEvent(Favorite favoriteReqObj, Boolean favoriteFlag) throws InvalidParameterException, AlreadyExistException, BusinessException, Exception {
        String result = null;
        try {
            favoriteReqObj = favoriteHelper.populateFavorite(favoriteReqObj);

            Optional<Event> eventEntity = eventRepository.findById(favoriteReqObj.getEvent().getId());
            if (!eventEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Optional<Viewer> viewerEntity = viewerRepository.findById(favoriteReqObj.getViewer().getId());
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            /*if (favoriteReqObj.getBroadcasterInfo() == null) {
                favoriteReqObj.setBroadcasterInfo(eventEntity.get().getBroadcasterInfo());
            } else {
                utils.isEmptyOrNull(favoriteReqObj.getBroadcasterInfo().getId(), "BroadcasterId");
                utils.isIntegerGreaterThanZero(favoriteReqObj.getBroadcasterInfo().getId(), "BroadcasterId");
            }*/

            if (favoriteFlag) {
                if (favoriteRepository.findByEventAndViewer(favoriteReqObj.getEvent(), favoriteReqObj.getViewer()) != null)
                    throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
                favoriteRepository.save(favoriteReqObj);
            } else {
                Favorite favoriteEntity = favoriteRepository.findByEventAndViewer(favoriteReqObj.getEvent(), favoriteReqObj.getViewer());
                if (favoriteEntity == null)
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
                favoriteRepository.delete(favoriteEntity);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all Viewer.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllViewers() throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Viewer> viewerList = null;
        try {
            viewerList = (List<Viewer>) viewerRepository.findAll();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (viewerList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWERS, viewerHelper.buildResponseObject(viewerList));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all SPotlightUsers with paging and orderBy.
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
    public String getAllViewers(Integer limit, Integer offset, String direction, String orderBy)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Viewer> viewerList = new ArrayList<Viewer>();
        int pageNum = offset / limit;
        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Viewer> viewers = viewerRepository.findAll(pageableRequest);
            totalCount = viewers.getTotalElements();
            List<Viewer> viewerEntities = viewers.getContent();
            for (Viewer viewerEntity : viewerEntities) {
                Viewer viewerDto = new Viewer();
                BeanUtils.copyProperties(viewerEntity, viewerDto);
                viewerList.add(viewerDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (viewerList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWERS, viewerHelper.buildResponseObject(viewerList));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, viewerList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get Viewer by viewerId.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getViewer(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Viewer> viewer = null;

        try {
            viewer = viewerRepository.findById(viewerId);
            if (!viewer.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObject(viewer.get()));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    @Override
    public String getOrderByViewer(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<Order> orders = null;

        try {
            orders = orderRepository.findByViewerId(viewerId);
            if (orders == null && orders.size() == 0)
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObjectForOrders(orders));
        result = utils.constructSucessJSON(jObj);
        return result;
    }


    /**
     * This method is used to get all Favorites Broadcasters with paging and orderBy.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getFavoriteBroadcasters(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;


//		BroadcasterInfoHelper broadcasterInfoHelper = new BroadcasterInfoHelper(genreRepository, spotlightUserHelper, genreHelper, spotlightCommissionRepository);
        Set<BroadcasterInfo> broadcasterInfoList = new HashSet<>();
        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            List<Favorite> favoriteList = favoriteRepository.findByViewer(viewerEntity.get());

            for (Favorite favorite : favoriteList) {
//				Optional<BroadcasterInfo> broadcasterInfoEntity = viewerRepository.find(favorite.getBroadcasterInfo());
                if (favorite.getBroadcasterInfo() != null)
                    broadcasterInfoList.add(favorite.getBroadcasterInfo());
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
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, viewerEntity.get()));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Favorites Broadcasters with paging and orderBy.
     *
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

    public String getFavoriteBroadcasters(Integer viewerId, Integer limit, Integer offset, String direction,
                                          String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;


        List<BroadcasterInfo> broadcasterInfoList = new ArrayList<>();
        List<Favorite> favoriteList = new ArrayList<>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			List<Favorite> favoriteList = favoriteRepository.findByViewer(viewerEntity);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Favorite> favorites = favoriteRepository.findByViewer(viewerEntity.get(), pageableRequest);
            totalCount = favorites.getTotalElements();
            List<Favorite> favoriteEntities = favorites.getContent();
            for (Favorite favoriteEntity : favoriteEntities) {
                Favorite favoriteDto = new Favorite();
                BeanUtils.copyProperties(favoriteEntity, favoriteDto);
                favoriteList.add(favoriteDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (favoriteList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        for (Favorite favorite : favoriteList) {
            if (favorite.getBroadcasterInfo() != null)
                broadcasterInfoList.add(favorite.getBroadcasterInfo());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, viewerEntity.get()));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Favorites Events.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getFavoriteEvents(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = new ArrayList<>();
        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            List<Favorite> favoriteList = favoriteRepository.findByViewer(viewerEntity.get());

            for (Favorite favorite : favoriteList) {
                if (favorite.getEvent() != null)
                    eventList.add(favorite.getEvent());
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (eventList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewerEntity.get(), null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Favorites Events with paging and orderBy.
     *
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

    public String getFavoriteEvents(Integer viewerId, Integer limit, Integer offset, String direction,
                                    String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<>();
        List<Favorite> favoriteList = new ArrayList<>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			List<Favorite> favoriteList = favoriteRepository.findByViewer(viewerEntity);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Favorite> favorites = favoriteRepository.findByViewer(viewerEntity.get(), pageableRequest);
            totalCount = favorites.getTotalElements();
            List<Favorite> favoriteEntities = favorites.getContent();
            for (Favorite favoriteEntity : favoriteEntities) {
                Favorite favoriteDto = new Favorite();
                BeanUtils.copyProperties(favoriteEntity, favoriteDto);
                favoriteList.add(favoriteDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (favoriteList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        for (Favorite favorite : favoriteList) {
            if (favorite.getEvent() != null)
                eventList.add(favorite.getEvent());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewerEntity.get(), null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Purchased Events.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getPurchasedEvents(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = new ArrayList<>();
        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            List<ViewerEvent> viewerEventList = viewerEventRepository.findByViewer(viewerEntity.get());

            for (ViewerEvent viewerEvent : viewerEventList) {
                if (viewerEvent.getEvent() != null)
                    eventList.add(viewerEvent.getEvent());
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (eventList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewerEntity.get(), null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Purchased Events with paging and orderBy.
     *
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

    public String getPurchasedEvents(Integer viewerId, Integer limit, Integer offset, String direction,
                                     String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<>();
        List<ViewerEvent> viewerEventList = new ArrayList<>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			List<Favorite> favoriteList = favoriteRepository.findByViewer(viewerEntity);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<ViewerEvent> viewerEvents = viewerEventRepository.findByViewer(viewerEntity.get(), pageableRequest);
            totalCount = viewerEvents.getTotalElements();
            List<ViewerEvent> viewerEventEntities = viewerEvents.getContent();
            for (ViewerEvent viewerEventEntity : viewerEventEntities) {
                ViewerEvent viewerEventDto = new ViewerEvent();
                BeanUtils.copyProperties(viewerEventEntity, viewerEventDto);
                viewerEventList.add(viewerEventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (viewerEventList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        for (ViewerEvent viewerEvent : viewerEventList) {
            if (viewerEvent.getEvent() != null)
                eventList.add(viewerEvent.getEvent());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewerEntity.get(), null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to change the password of Spotlight User.
     *
     * @param viewer: Viewer
     * @return String: Response
     * @throws BusinessException BusinessException
     * @throws Exception         Exception
     */
    @Override
    public String changeViewerPassword(Viewer viewer) throws BusinessException, Exception {
        String result = null;

        Viewer viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findByUsername(viewer.getUsername());
            viewerEntity.setPassword(passwordEncoder.encode(viewer.getPassword()));
            viewerRepository.save(viewerEntity);
            viewerSessionRepository.deleteByViewer(viewerEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        try {
            emailNotificationService.sendMimeMessage(
                    "You’ve got a new password! <BR> <BR> You’ve successfully updated your password!",
                    viewerEntity.getEmail(), viewerEntity.getEmail(), IConstants.EMAIL_SUBJECT_PASSWORD_CHANGE);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while sending email ::::: " + e.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObject(viewerEntity));
        result = utils.constructSucessJSON(jObj);
        return result;

    }

    /**
     * This method is used to verify the old password of Spotlight User.
     *
     * @param viewer:      Viewer
     * @param oldPassword: String
     * @return String: Response
     * @throws BusinessException BusinessException
     * @throws Exception         Exception
     */
    public boolean verifyOldPassword(Viewer viewer, String oldPassword) throws BusinessException, Exception {
        boolean result = false;
        Viewer viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findByUsername(viewer.getUsername());
            String persistedPassword = viewerEntity.getPassword();
            if (passwordEncoder.matches(oldPassword, persistedPassword)) {
                return true;
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        return result;

    }

    /**
     * This method is used to update the Viewer.
     *
     * @param viewerReqObj: Viewer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String updateViewer(Viewer viewerReqObj)
            throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;

        Optional<Viewer> viewerEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerReqObj.getId());
            if (!viewerEntity.isPresent()) {
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }

            if (viewerReqObj.getPhone() != null) {
                Viewer userEntity =
                        viewerRepository.findByPhone(viewerReqObj.getPhone());
                if ((userEntity != null) &&
                        userEntity.getPhone().equals(viewerReqObj.getPhone())) {
                    throw new AlreadyExistException("User with same Phone already exists.");
                }
            }

            Viewer viewerEntity_ = viewerHelper.populateViewer(viewerReqObj, viewerEntity.get());
            Viewer viewer = viewerRepository.save(viewerEntity_);

            SpotlightUser byEmail = spotlightUserRepository.findByEmail(viewer.getEmail());
            if (byEmail != null) {
                byEmail.setProfileUrl(viewer.getProfilePicture());
                spotlightUserRepository.save(byEmail);
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.VIEWER, viewerHelper.buildResponseObject(viewerEntity.get()));
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to delete the Viewer by id.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String deleteViewer(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Viewer> viewer = null;
        try {
            viewer = viewerRepository.findById(viewerId);
            if (!viewer.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            viewerRepository.delete(viewer.get());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new InvalidParameterException("We cannot delete the Viewer as there is associated data with the Viewer. Please deactivate it.");
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
        JSONObject jObj = new JSONObject();

        result = utils.constructSucessJSON(jObj);
        return result;
    }

    @Override
    public String updateViewerChatAccess(Viewer viewerReqObj, int eventId, String access, boolean accessFlag)
            throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;

        Viewer viewerEntity = null;
        Optional<Event> eventEntity = null;
        try {
            viewerEntity = viewerRepository.findByChatName(viewerReqObj.getChatName());
            if (viewerEntity == null) {
                throw new ResourceNotFoundException("Viewer " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }

            eventEntity = eventRepository.findById(eventId);
            if (!eventEntity.isPresent()) {
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }

            List<ViewerEvent> viewerEvents = viewerEventRepository.findByEventAndViewer(eventEntity.get(), viewerEntity);
            String viewerChatAuthKey = "";
            if (viewerEvents != null && viewerEvents.size() > 0) {
                viewerChatAuthKey = viewerEvents.get(0).getViewerChatAuthKey();
                LOGGER.info("viewerChatAuthKey :::: " + viewerChatAuthKey);
            } else {
                throw new ResourceNotFoundException("Viewer is not subscribed for the Event.");
            }

            List<String> authList = new ArrayList<>();
            authList.add(viewerChatAuthKey);
            try {
                PubNubService pns = new PubNubService();
                pns.grantPermissions(authList, eventEntity.get().getUniqueName(), access, accessFlag);

            } catch (Exception e) {
                LOGGER.error("ERROR in grantpermissions :::: " + e.getMessage());
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        result = utils.constructSucessJSON(jObj);

        return result;
    }


    public String manageFavoriteEventType(Favorite favoriteReqObj, Boolean favoriteFlag) throws InvalidParameterException, AlreadyExistException, BusinessException, Exception {
        String result = null;
        try {
            favoriteReqObj = favoriteHelper.populateFavorite(favoriteReqObj);

            Optional<EventType> eventTypeEntity = eventTypeRepository.findById(favoriteReqObj.getEventType().getId());
            if (!eventTypeEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Optional<Viewer> viewerEntity = viewerRepository.findById(favoriteReqObj.getViewer().getId());
            if (!viewerEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Optional<Event> eventEntity = eventRepository.findById(favoriteReqObj.getEvent().getId());
            if (!eventEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			/*if(favoriteReqObj.getBroadcasterInfo() == null) {
				favoriteReqObj.setBroadcasterInfo(eventEntity.get().getBroadcasterInfo());
			} else {
				utils.isEmptyOrNull(favoriteReqObj.getBroadcasterInfo().getId(), "BroadcasterId");
				utils.isIntegerGreaterThanZero(favoriteReqObj.getBroadcasterInfo().getId(), "BroadcasterId");
			}*/

            if (favoriteFlag) {
                if (favoriteRepository.findByEventAndEventTypeAndViewer(favoriteReqObj.getEvent(), favoriteReqObj.getEventType(), favoriteReqObj.getViewer()) != null)
                    throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
                favoriteReqObj = favoriteRepository.save(favoriteReqObj);
            } else {
                Favorite favoriteEntity = favoriteRepository.findByEventAndEventTypeAndViewer(favoriteReqObj.getEvent(), favoriteReqObj.getEventType(), favoriteReqObj.getViewer());
                if (favoriteEntity == null)
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
                favoriteRepository.delete(favoriteEntity);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        result = utils.constructSucessJSON(jObj);

        return result;
    }


    public String orderEvent(Integer viewerId, Integer eventId, Order orderReqObj)
            throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;

        Optional<Viewer> viewerEntity = null;
        Optional<Event> eventEntity = null;
        Wallet walletEntity = null;
        try {
            viewerEntity = viewerRepository.findById(viewerId);
            if (!viewerEntity.isPresent()) {
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            eventEntity = eventRepository.findById(eventId);
            if (!eventEntity.isPresent()) {
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            walletEntity = walletRepository.findByViewerId(viewerEntity.get().getId());
            Double minusAmount = null;
            if (walletEntity != null) {
                if (walletEntity.getAmount() != null) {
                    if (walletEntity.getAmount() >= eventEntity.get().getActualPrice()) {
                        minusAmount = (walletEntity.getAmount() - eventEntity.get().getActualPrice());
                        walletEntity.setAmount(minusAmount);
                        walletRepository.save(walletEntity);
                    } else {
                        throw new ResourceNotFoundException("Insufficient Funds !");
                    }
                } else {
                    throw new ResourceNotFoundException("wallet amount can not be null or empty !");
                }
            } else {
                throw new ResourceNotFoundException("Wallet not exist yet !");
            }
            Order order = new Order();
            order.setQuantity(orderReqObj.getQuantity());
            order.setEventId(eventEntity.get().getId());
            order.setViewerId(viewerEntity.get().getId());
            order.setPrice(eventEntity.get().getActualPrice());
            order.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
            order.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            order = orderRepository.save(order);

            JSONObject jObj = new JSONObject();
            JSONObject viewerObj = new JSONObject();
            viewerObj.put("message", "order completed successfully !");
            //  viewerObj.put("Order",order);
            jObj.put("Order", viewerObj);
            result = utils.constructSucessJSON(jObj);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
        return result;
    }

    @Override
    public String orderEvent(List<Viewer> viewerId, Integer eventId, Order orderReqObj, List<String> inValidEmails) throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;
        Optional<Event> eventEntity = null;
        try {
            eventEntity = eventRepository.findById(eventId);
            if (!eventEntity.isPresent()) {
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            for (Viewer viewer : viewerId) {
                Order order = new Order();
                order.setQuantity(orderReqObj.getQuantity());
                order.setEventId(eventEntity.get().getId());
                order.setViewerId(viewer.getId());
                order.setPrice(0.0f);
                order.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
                order.setCreatedOn(new Timestamp(System.currentTimeMillis()));
                order = orderRepository.save(order);
            }

            JSONObject jObj = new JSONObject();
            JSONObject viewerObj = new JSONObject();
            viewerObj.put("message", "order completed successfully !");
            if (inValidEmails.size() > 0)
                viewerObj.put("NotExistEmails", inValidEmails.toString());
            jObj.put("Order", viewerObj);
            result = utils.constructSucessJSON(jObj);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
        return result;
    }


}
