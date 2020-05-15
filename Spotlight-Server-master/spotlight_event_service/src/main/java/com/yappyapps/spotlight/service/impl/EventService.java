package com.yappyapps.spotlight.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yappyapps.spotlight.domain.helper.WowzaEventDeleted;
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

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventReview;
import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.domain.LiveStream;
import com.yappyapps.spotlight.domain.LiveStreamConfig;
import com.yappyapps.spotlight.domain.PricingRule;
import com.yappyapps.spotlight.domain.SpotlightCommission;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.helper.EventHelper;
import com.yappyapps.spotlight.domain.helper.EventReviewHelper;
import com.yappyapps.spotlight.domain.helper.SpotlightCommissionHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.repository.IEventReviewRepository;
import com.yappyapps.spotlight.repository.IEventTypeRepository;
import com.yappyapps.spotlight.repository.IGenreRepository;
import com.yappyapps.spotlight.repository.ILiveStreamConfigRepository;
import com.yappyapps.spotlight.repository.IPricingRuleRepository;
import com.yappyapps.spotlight.repository.ISpotlightCommissionRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.IViewerRepository;
import com.yappyapps.spotlight.service.IEventService;
import com.yappyapps.spotlight.util.AmazonClient;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;
import org.springframework.web.client.HttpClientErrorException;

/**
 * The EventService class is the implementation of IEventService
 *
 * <h1>@Service</h1> denotes that it is a service class
 * *
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class EventService implements IEventService {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    /**
     * IEventRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventRepository eventRepository;

    /**
     * IGenreRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IGenreRepository genreRepository;
    /**
     * IBroadcasterInfoRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IBroadcasterInfoRepository broadcasterInfoRepository;

    /**
     * IEventTypeRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventTypeRepository eventTypeRepository;

    /**
     * ISpotlightCommissionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ISpotlightCommissionRepository spotlightCommissionRepository;

    /**
     * IViewerRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerRepository viewerRepository;

    /**
     * IEventReviewRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventReviewRepository eventReviewRepository;

    /*
     * ISpotlightUserRepository Bean
     */
    @Autowired
    private ISpotlightUserRepository spotlightUserRepository;

    /*
     * ILiveStreamConfigRepository Bean
     */
    @Autowired
    private ILiveStreamConfigRepository liveStreamConfigRepository;

    /*
     * IPricingRuleRepository Bean
     */
    @Autowired
    private IPricingRuleRepository pricingRuleRepository;

    /**
     * EventHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private EventHelper eventHelper;

    /**
     * EventReviewHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private EventReviewHelper eventReviewHelper;

    /**
     * SpotlightCommissionHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private SpotlightCommissionHelper spotlightCommissionHelper;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    /**
     * AmazonClient dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private AmazonClient amazonClient;

    /**
     * This method is used to create the Event
     *
     * @param eventReqObj: Event
     * @return String: Response
     * @throws AlreadyExistException AlreadyExistException
     * @throws BusinessException     BusinessException
     * @throws Exception             Exception
     */
    @Override
    public String createEvent(Event eventReqObj) throws AlreadyExistException, BusinessException, Exception {
        String result = null;

        Event eventEntity = null;
        Float commission = eventReqObj.getCommission();
        try {
            Optional<PricingRule> pricingRuleEntity = pricingRuleRepository.findById(eventReqObj.getPricingRule().getId());

            if (pricingRuleEntity.isPresent()) {
                if (eventReqObj.getActualPrice() < pricingRuleEntity.get().getMinimumPrice()) {
                    throw new InvalidParameterException("Actual Price cannot be less than " + pricingRuleEntity.get().getMinimumPrice() + ".");
                }

                if (eventReqObj.getEventDuration() > pricingRuleEntity.get().getEventDuration()) {
                    throw new InvalidParameterException("Event Duration cannot be greater than " + pricingRuleEntity.get().getEventDuration() + ".");
                }

            }

            eventEntity = eventHelper.populateEvent(eventReqObj);

            WowzaClient wowzaClient = new WowzaClient(eventEntity.getLiveStreamConfig());
            String wowzaResponse = "";
            String wowzaScheduleResponse = "";
            JSONObject wowzaJSONObject = new JSONObject();
            if (eventEntity.getLiveStreamConfig().getConnectionType().equalsIgnoreCase("Cloud")) {
                LiveStream liveStream = new LiveStream();
                liveStream.setName(eventEntity.getDisplayName());
                JSONObject jObj = new JSONObject();
                jObj.put("live_stream", liveStream.getJSONObject());

                //// create live stream on cloud
                wowzaResponse = wowzaClient.executePost("live_streams", jObj);
                LOGGER.info("wowzaResponse :::: " + wowzaResponse);
                wowzaJSONObject = new JSONObject(wowzaResponse.toString());

                try {
                    JSONObject transcoderObj = new JSONObject();
                    transcoderObj.put("transcoder", new JSONObject().put("idle_timeout", 300).put("low_latency",true));
                    wowzaScheduleResponse = wowzaClient.executePatch("transcoders/" + wowzaJSONObject.getJSONObject("live_stream").get("id").toString(), transcoderObj);
                    wowzaJSONObject.getJSONObject("live_stream").put("transcoder",new JSONObject(wowzaScheduleResponse));
                    JSONObject property = new JSONObject();
                    property.put("property", new JSONObject().put("key", "chunkSize").put("section","hls").put("value",2));
                    String stream_targets = wowzaClient.executePost("stream_targets/" + wowzaJSONObject.getJSONObject("live_stream").getJSONArray("stream_targets").getJSONObject(0).get("id").toString()+"/properties", property);
                    wowzaJSONObject.getJSONObject("live_stream").put("stream_targets", new JSONObject(stream_targets));

                } catch (Exception e) {
                    LOGGER.error("Error in updating transcoder idle_timeout");
                }

                liveStream.setWowzaEventId(wowzaJSONObject.getJSONObject("live_stream").get("id").toString());//eventEntity.getTimezone()
                Date startTranscoderDate = new Date(utils.convertOtherTimeZoneToUTC(eventEntity.getEventUtcDatetime(), "UTC").getTime() - 3 * 60 * 1000);
                Date stopTranscoderDate = new Date(utils.convertOtherTimeZoneToUTC(eventEntity.getEventUtcDatetime(), "UTC").getTime() + eventEntity.getEventDuration() * 60 * 1000 + 3 * 60 * 1000);
                liveStream.setStartTranscoderDate(startTranscoderDate);
                liveStream.setStopTranscoderDate(stopTranscoderDate);

                try {
                    String schedules = wowzaClient.executePost("schedules", liveStream.getCloudJSONObjectForSchedule(wowzaJSONObject.getJSONObject("live_stream").getJSONObject("transcoder").getJSONObject("transcoder").get("id").toString(),new Timestamp(startTranscoderDate.getTime()),new Timestamp(stopTranscoderDate.getTime())));
                    if(schedules != null)
                        wowzaJSONObject.getJSONObject("live_stream").put("schedule", new JSONObject(schedules));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Could not schedule the event :::: ");
                }

            } else if (eventEntity.getLiveStreamConfig().getConnectionType().equalsIgnoreCase("Server")) {
                LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
                wowzaClient = new WowzaClient(liveStreamConfig);
                LiveStream liveStream = new LiveStream();
                liveStream.setName(eventEntity.getDisplayName());
                liveStream.setUri(eventEntity.getLiveStreamConfig().getHost());
//				JSONObject jObj = new JSONObject();
//				jObj.put("live_stream", liveStream.getJSONObject());
                wowzaResponse = wowzaClient.executePost("live_streams", liveStream.getCloudJSONObjectForWebRtc(eventEntity.getDisplayName()));
//				wowzaResponse = wowzaClient.executeGet("applications/"+liveStream.getName());
                LOGGER.info("wowzaResponse :::: " + wowzaResponse);
                wowzaJSONObject = new JSONObject(wowzaResponse.toString());

                try {
                    JSONObject transcoderObj = new JSONObject();
                    JSONObject jObj = new JSONObject();
                    jObj.put("billing_mode", "pay_as_you_go");
                    jObj.put("broadcast_location", "us_west_california");
                    jObj.put("delivery_method", "push");
                    jObj.put("protocol", "webrtc");
                    jObj.put("name", eventEntity.getDisplayName());
                    jObj.put("transcoder_type", "transcoded");
                    jObj.put("idle_timeout", 300);
                    jObj.put("low_latency",true);
                    transcoderObj.put("transcoder",jObj);
                    String  transcoders = wowzaClient.executePatch("transcoders/" + wowzaJSONObject.getJSONObject("live_stream").get("id").toString(), transcoderObj);
                    wowzaJSONObject.getJSONObject("live_stream").put("transcoder",new JSONObject(transcoders));
                    JSONObject property = new JSONObject();
                    property.put("property", new JSONObject().put("key", "chunkSize").put("section","hls").put("value",2));
                    String stream = wowzaClient.executePost("stream_targets/" + wowzaJSONObject.getJSONObject("live_stream").getJSONArray("stream_targets").getJSONObject(0).get("id").toString()+"/properties", property);
                    wowzaJSONObject.getJSONObject("live_stream").put("stream_targets",new JSONObject(stream));


                } catch (Exception e) {
                    LOGGER.error("Error in updating transcoder idle_timeout");
                }

                liveStream.setWowzaEventId(wowzaJSONObject.getJSONObject("live_stream").get("id").toString());
                Date startTranscoderDate = new Date(eventEntity.getEventUtcDatetime().getTime() - 3 * 60 * 1000);
                Date stopTranscoderDate = new Date(eventEntity.getEventUtcDatetime().getTime () + eventEntity.getEventDuration() * 60 * 1000 + 3 * 60 * 1000);
                liveStream.setStartTranscoderDate(startTranscoderDate);
                liveStream.setStopTranscoderDate(stopTranscoderDate);
				String schedules = wowzaClient.executePost("schedules", liveStream.getCloudJSONObjectForSchedule(wowzaJSONObject.getJSONObject("live_stream").getJSONObject("transcoder").getJSONObject("transcoder").get("id").toString(),new Timestamp(startTranscoderDate.getTime()),new Timestamp(stopTranscoderDate.getTime())));
                wowzaJSONObject.getJSONObject("live_stream").put("schedule", new JSONObject(schedules));

               //String connectionCode = wowzaJSONObject.getJSONObject("live_stream").getString("connection_code");
               // String streamName = utils.generateRandomString(16);

                wowzaJSONObject.put("appName", "webrtc");
                wowzaJSONObject.put("streamName", wowzaJSONObject.getJSONObject("live_stream").getJSONObject("source_connection_information").get("stream_name"));

                wowzaClient = new WowzaClient(eventEntity.getLiveStreamConfig());
               // wowzaResponse = wowzaClient.executePost("applications/webrtc/pushpublish/mapentries/" + streamName, liveStream.getJSONObjectForServer(connectionCode, streamName));
                //wowzaResponse = wowzaClient.executeGet("applications/webrtc/pushpublish/mapentries/" + streamName);
                LOGGER.info("wowzaResponse :::: " + wowzaResponse);

                //// create application on Engine
                //// create live stream on cloud with source wowza engine
                //// create target setting on Engine with connection code
            }
            eventEntity.setLiveStreamData(wowzaJSONObject.toString());

            String adminChatAuthKey = utils.generateRandomString(32);
            String broadcasterChatAuthKey = utils.generateRandomString(32);
            eventEntity.setAdminChatAuthKey(adminChatAuthKey);
            eventEntity.setBroadcasterChatAuthKey(broadcasterChatAuthKey);
            List<String> authList = new ArrayList<>();
            authList.add(adminChatAuthKey);
            authList.add(broadcasterChatAuthKey);
            try {
                PubNubService pns = new PubNubService();
                pns.grantPermissions(authList, eventEntity.getUniqueName(), true, true, true);

            } catch (Exception e) {
                LOGGER.error("ERROR in grantpermissions :::: " + e.getMessage());
            }

            //eventEntity.setTimezone("PST");
            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            //eventEntity.setEventUtcDatetime(timestamp);
            eventEntity = eventRepository.save(eventEntity);
            if (commission != null) {
                SpotlightCommission spotlightCommission = new SpotlightCommission();
                spotlightCommission.setBroadcasterInfo(eventEntity.getBroadcasterInfo());
                spotlightCommission.setPercentage(commission);
                spotlightCommission.setEvent(eventEntity);
                spotlightCommission = spotlightCommissionHelper.populateSpotlightCommission(spotlightCommission);
                spotlightCommissionRepository.save(spotlightCommission);
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            this.amazonClient.deleteFileFromS3Bucket(eventReqObj.getEventImageUrl());
            this.amazonClient.deleteFileFromS3Bucket(eventReqObj.getEventVideoUrl());
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            this.amazonClient.deleteFileFromS3Bucket(eventReqObj.getEventImageUrl());
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(eventEntity, null, true, new EventType()));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Events.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllEvents() throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        try {
            //eventList = (List<Event>) eventRepository.findAllByEventUtcDatetimeGreaterThanEqual();
            eventList = (List<Event>) eventRepository.findAll();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }


    @Override
    public String getOnlyAllUpcomingEvent() throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        try {
            eventList = (List<Event>) eventRepository.findAllByEventUtcDatetimeGreaterThanEqual();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Events by EventType.
     *
     * @param eventTypeId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllEvents(Integer eventTypeId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        try {
            Optional<EventType> eventTypeEntity = eventTypeRepository.findById(eventTypeId);
            if (!eventTypeEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

          //  eventList = (List<Event>) eventRepository.findByEventType(eventTypeEntity.get());
            eventList = (List<Event>) eventRepository.findByEventType(eventTypeEntity.get());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    @Override
    public String getAllEventsUpComing(Integer eventTypeId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        try {
            Optional<EventType> eventTypeEntity = eventTypeRepository.findById(eventTypeId);
            if (!eventTypeEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            eventList = (List<Event>) eventRepository.findByEventType(eventTypeEntity.get());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }


    @Override
    public String getAllEvents(Integer eventTypeId, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        Optional<EventType> eventTypeEntity = null;
        try {
            if (eventTypeId != null) {
                eventTypeEntity = eventTypeRepository.findById(eventTypeId);
                if (!eventTypeEntity.isPresent())
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            if (viewerId != null) {
                viewerEntity = viewerRepository.findById(viewerId);
                if (!viewerEntity.isPresent())
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }


            if (eventTypeEntity != null)
                eventList = (List<Event>) eventRepository.findByEventType(eventTypeEntity.get());
            else
                eventList = (List<Event>) eventRepository.findAll();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewerEntity != null ? viewerEntity.get() : null, eventTypeEntity != null ? eventTypeEntity.get() : null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    @Override
    public String getAllEventsUpcoming(Integer eventTypeId, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        Optional<EventType> eventTypeEntity = null;
        try {
            if (eventTypeId != null) {
                eventTypeEntity = eventTypeRepository.findById(eventTypeId);
                if (!eventTypeEntity.isPresent())
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            if (viewerId != null) {
                viewerEntity = viewerRepository.findById(viewerId);
                if (!viewerEntity.isPresent())
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }


            if (eventTypeEntity != null)
                eventList = (List<Event>) eventRepository.findByEventType(eventTypeEntity.get());
            else
                eventList = (List<Event>) eventRepository.findAllByEventUtcDatetimeGreaterThanEqual();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewerEntity != null ? viewerEntity.get() : null, eventTypeEntity != null ? eventTypeEntity.get() : null));

        result = utils.constructSucessJSON(jObj);

        return result;

    }


    @Override
    public String getEventStart(Integer eventId, Integer spotlightId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        Optional<SpotlightUser> spotlightUserEntity = null;
        try {
            if (spotlightId != null) {
                spotlightUserEntity = spotlightUserRepository.findById(spotlightId);
                if (!spotlightUserEntity.isPresent())
                    throw new ResourceNotFoundException("SpotlightUser " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
            WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
            if (event.get().getLiveStreamData() != null) {
                JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
                String wowzaResponse = "";
                if (liveStreamData.has("live_stream")) {
                    try {
                        JSONObject liveStreamJObj = new JSONObject(liveStreamData.get("live_stream").toString());
                        String liveStreamId = liveStreamJObj.get("id").toString();
                        WowzaEventDeleted wowzaEventDeleted = null;
                        try {
                            wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
                            wowzaEventDeleted = new Gson().fromJson(wowzaResponse, WowzaEventDeleted.class);
                        } catch (HttpClientErrorException clientError) {
                            event.get().setLiveStreamState("The requested resource has been deleted or not found");
                        }
                        // WowzaEventDeleted wowzaEventDeleted = new Gson().fromJson(wowzaResponse, WowzaEventDeleted.class);
                        if (wowzaEventDeleted != null && wowzaEventDeleted.getMeta() != null) {
                            String message = wowzaEventDeleted.getMeta().getMessage();
                            event.get().setLiveStreamState(message);
                        } else {
                            String state = new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString();
                            event.get().setLiveStreamState(state);
                            eventRepository.save(event.get());
                            if (!state.equals("starting")) {
                                wowzaResponse = wowzaClient.executePut("live_streams/" + liveStreamId + "/start");
                                String stateStart = new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString();
                                event.get().setLiveStreamState(stateStart);
                            } else if (state.equals("starting")) {
                                event.get().setLiveStreamState("already starting");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
                        LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
                    }
                }
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();

        jObj.put("live_stream", new JSONObject().put("state", event.get().getLiveStreamState()));
        result = utils.constructSucessJSON(jObj);
        return result;
    }


    @Override
    public String getEventStop(Integer eventId, Integer spotlightId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        Optional<SpotlightUser> spotlightUserEntity = null;
        try {
            if (spotlightId != null) {
                spotlightUserEntity = spotlightUserRepository.findById(spotlightId);
                if (!spotlightUserEntity.isPresent())
                    throw new ResourceNotFoundException("SpotlightUser " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
            WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
            if (event.get().getLiveStreamData() != null) {
                JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
                String wowzaResponse = "";
                if (liveStreamData.has("live_stream")) {
                    try {
                        JSONObject liveStreamJObj = new JSONObject(liveStreamData.get("live_stream").toString());
                        String liveStreamId = liveStreamJObj.get("id").toString();
                        WowzaEventDeleted wowzaEventDeleted = null;
                        try {
                            wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
                            wowzaEventDeleted = new Gson().fromJson(wowzaResponse, WowzaEventDeleted.class);
                        } catch (HttpClientErrorException clientError) {
                            event.get().setLiveStreamState("The requested resource has been deleted or not found");
                        }
                        if (wowzaEventDeleted != null && wowzaEventDeleted.getMeta() != null) {
                            String message = wowzaEventDeleted.getMeta().getMessage();
                            event.get().setLiveStreamState(message);
                            eventRepository.save(event.get());

                        } else {
                            //wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
                            String state = new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString();
                            event.get().setLiveStreamState(state);
                            eventRepository.save(event.get());
                            if (!state.equals("stopped")) {
                                wowzaResponse = wowzaClient.executePut("live_streams/" + liveStreamId + "/stop");
                                String stateStart = new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString();
                                event.get().setLiveStreamState(stateStart);
                            } else if (state.equals("stopped")) {
                                event.get().setLiveStreamState("already stopped");
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
                        LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
                    }
                }
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put("live_stream", new JSONObject().put("state", event.get().getLiveStreamState()));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get all Events by EventType Name.
     *
     * @param eventTypeName: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllEventsByEventTypeName(String eventTypeName) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        EventType eventTypeEntity = null;
        try {
            eventTypeEntity = eventTypeRepository.findByName(eventTypeName);
            if (eventTypeEntity == null)
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            eventList = (List<Event>) eventRepository.findByEventType(eventTypeEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, eventTypeEntity));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Events with paging.
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
    public String getAllEvents(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;

        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findAll(pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());


        result = utils.constructSucessJSON(jObj);

        return result;

    }


    @Override
    public String getAllEventsWithViewer(Integer limit, Integer offset, String direction, String orderBy, Integer viewerId, Integer eventTypeId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        Optional<Viewer> viewer = null;
        Optional<EventType> eventType = null;
        try {
            if (viewerId != null) {
                viewer = viewerRepository.findById(viewerId);
                if (!viewer.isPresent())
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
            if (eventTypeId != null) {
                eventType = eventTypeRepository.findById(viewerId);
                if (!eventType.isPresent())
                    throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }

            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findAll(pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, viewer.isPresent() ? viewer.get() : null, eventType.isPresent() ? eventType.get() : null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());


        result = utils.constructSucessJSON(jObj);

        return result;

    }


    /**
     * This method is used to get all Events by EventType with paging.
     *
     * @param eventTypeId: Integer
     * @param limit:       Integer
     * @param offset:      Integer
     * @param direction:   String
     * @param orderBy:     String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllEvents(Integer eventTypeId, Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;

        try {
            Optional<EventType> eventTypeEntity = eventTypeRepository.findById(eventTypeId);
            if (!eventTypeEntity.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findByEventType(eventTypeEntity.get(), pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());


        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Events by EventType with paging.
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
    public String getAllEventsByEventTypeName(String eventTypeName, Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;

        try {
            EventType eventTypeEntity = eventTypeRepository.findByName(eventTypeName);
            if (eventTypeEntity == null)
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findByEventType(eventTypeEntity, pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());


        result = utils.constructSucessJSON(jObj);

        return result;

    }


    /**
     * This method is used to get all Events by event id.
     *
     * @param eventId:  Integer
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEvent(Integer eventId, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
//			WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
//			if(event.get().getLiveStreamData() != null) {
//				JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
//				String wowzaResponse = "";
//				if(liveStreamData.has("live_stream")) {
//					try {
//						JSONObject liveStreamJObj= new JSONObject(liveStreamData.get("live_stream").toString());
//						String liveStreamId = liveStreamJObj.get("id").toString();
//						wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
//						event.get().setLiveStreamState(new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//						LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
//						LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
//					}
//				}
//			}
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(event.get(), (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, true, new EventType()));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    @Override
    public String getEvent(Integer eventId, Integer viewerId, SpotlightUser spotlightUser) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
//			WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
//			if(event.get().getLiveStreamData() != null) {
//				JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
//				String wowzaResponse = "";
//				if(liveStreamData.has("live_stream")) {
//					try {
//						JSONObject liveStreamJObj= new JSONObject(liveStreamData.get("live_stream").toString());
//						String liveStreamId = liveStreamJObj.get("id").toString();
//						wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
//						event.get().setLiveStreamState(new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//						LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
//						LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
//					}
//				}
//			}
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(event.get(), (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, true, (spotlightUser != null ? spotlightUser : null)));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    @Override
    public String getEvent(Integer eventId, Integer viewerId, BroadcasterInfo broadcasterInfo) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
//			WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
//			if(event.get().getLiveStreamData() != null) {
//				JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
//				String wowzaResponse = "";
//				if(liveStreamData.has("live_stream")) {
//					try {
//						JSONObject liveStreamJObj= new JSONObject(liveStreamData.get("live_stream").toString());
//						String liveStreamId = liveStreamJObj.get("id").toString();
//						wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
//						event.get().setLiveStreamState(new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//						LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
//						LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
//					}
//				}
//			}
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(event.get(), (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, true, (broadcasterInfo != null ? broadcasterInfo : null)));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    @Override
    public String getEvent(Integer eventId, Integer viewerId, Viewer viewer) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//			LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
//			WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
//			if(event.get().getLiveStreamData() != null) {
//				JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
//				String wowzaResponse = "";
//				if(liveStreamData.has("live_stream")) {
//					try {
//						JSONObject liveStreamJObj= new JSONObject(liveStreamData.get("live_stream").toString());
//						String liveStreamId = liveStreamJObj.get("id").toString();
//						wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
//						event.get().setLiveStreamState(new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//						LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
//						LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
//					}
//				}
//			}
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(event.get(), (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, true, (viewer != null ? viewer : null)));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    @Override
    public String getEventLiveStreamStatus(Integer eventId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;

        try {
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            LiveStreamConfig liveStreamConfig = liveStreamConfigRepository.findByConnectionType("Cloud");
            WowzaClient wowzaClient = new WowzaClient(liveStreamConfig);
            if (event.get().getLiveStreamData() != null) {
                JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
                String wowzaResponse = "";
                if (liveStreamData.has("live_stream")) {
                    try {
                        JSONObject liveStreamJObj = new JSONObject(liveStreamData.get("live_stream").toString());
                        String liveStreamId = liveStreamJObj.get("id").toString();
                        wowzaResponse = wowzaClient.executeGet("live_streams/" + liveStreamId + "/state");
                        event.get().setLiveStreamState(new JSONObject(wowzaResponse).getJSONObject("live_stream").get("state").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("Exception in getting Event state from Wowza  " + e.getMessage());
                        LOGGER.error("Exception in getting Event state from Wowza Cloud for EventId " + eventId);
                    }
                }
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, new JSONObject().put("liveStreamState", event.get().getLiveStreamState()));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get all Events by event name.
     *
     * @param eventName:       String
     * @param broadcasterInfo: BroadcasterInfo
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEventByName(String eventName, BroadcasterInfo broadcasterInfo) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<Event> eventList = null;

        try {
            eventList = eventRepository.findByBroadcasterInfoAndDisplayNameContaining(broadcasterInfo, eventName);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(eventList, null, null));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get all Events by Broadcaster.
     *
     * @param viewerId:      Integer
     * @param broadcasterId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEventsByBroadcaster(Integer viewerId, Integer broadcasterId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        BroadcasterInfo broadcasterInfo = null;
        Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(broadcasterId);
        if (broadcasterInfoEntity.isPresent())
            broadcasterInfo = broadcasterInfoEntity.get();

        if (broadcasterInfo == null) {
            Optional<SpotlightUser> spotlightUser = spotlightUserRepository
                    .findById(broadcasterId);
            if (spotlightUser.isPresent()) {
                broadcasterInfo = broadcasterInfoRepository
                        .findBySpotlightUser(spotlightUser.get());
            }
        }
        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
           // eventList = (List<Event>) eventRepository.findByBroadcasterInfo(broadcasterInfo);
            eventList = (List<Event>)eventRepository.findByBroadcasterInfo(broadcasterInfo);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    @Override
    public String getEventsByBroadcasterUpcoming(Integer viewerId, Integer broadcasterId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        BroadcasterInfo broadcasterInfo = null;
        Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(broadcasterId);
        if (broadcasterInfoEntity.isPresent())
            broadcasterInfo = broadcasterInfoEntity.get();

        if (broadcasterInfo == null) {
            Optional<SpotlightUser> spotlightUser = spotlightUserRepository
                    .findById(broadcasterId);
            if (spotlightUser.isPresent()) {
                broadcasterInfo = broadcasterInfoRepository
                        .findBySpotlightUser(spotlightUser.get());
            }
        }
        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            eventList = (List<Event>) eventRepository.findByBroadcasterInfo(broadcasterInfo);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));

        result = utils.constructSucessJSON(jObj);

        return result;
    }


    /**
     * This method is used to get all Events by broadcaster with paging.
     *
     * @param viewerId:      Integer
     * @param broadcasterId: Integer
     * @param limit:         Integer
     * @param offset:        Integer
     * @param direction:     String
     * @param orderBy:       String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEventsByBroadcaster(Integer viewerId, Integer broadcasterId, Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;
        BroadcasterInfo broadcasterInfo = null;
        Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(broadcasterId);
        if (broadcasterInfoEntity.isPresent())
            broadcasterInfo = broadcasterInfoEntity.get();

        if (broadcasterInfo == null) {
            Optional<SpotlightUser> spotlightUser = spotlightUserRepository
                    .findById(broadcasterId);
            if (spotlightUser.isPresent()) {
                broadcasterInfo = broadcasterInfoRepository
                        .findBySpotlightUser(spotlightUser.get());
            }
        }
        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findByBroadcasterInfo(broadcasterInfo, pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all the Events by viewerId with paging.
     *
     * @param viewerId:  Integer
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String getAllViewerEvents(Integer viewerId, Integer limit, Integer offset, String direction,
                                     String orderBy) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findAll(pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }


    /**
     * This method is used to get all the Events by viewerId.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String getAllViewerEvents(Integer viewerId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            eventList = (List<Event>) eventRepository.findAll();

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all Events by status.
     *
     * @param status: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEventsByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<Event> eventList = null;

        try {
            eventList = eventRepository.findAllByStatus(status);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all Events by status with paging.
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
    @Override
    public String getEventByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;


        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findAllByStatus(status, pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all trending Events.
     *
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getTrendingEvents(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);

            eventList = (List<Event>) eventRepository.findByIsTrendingAndEventUtcDatetimeGreaterThan(true, new Timestamp(System.currentTimeMillis()));
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));

        result = utils.constructSucessJSON(jObj);

        return result;


    }

    /**
     * This method is used to get all trending Events by status.
     *
     * @param status:   String
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */

    public String getTrendingEvents(String status, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            eventList = eventRepository.findByStatusAndIsTrendingAndEventUtcDatetimeGreaterThan(status, true, new Timestamp(System.currentTimeMillis()));
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        result = utils.constructSucessJSON(jObj);

        return result;


    }

    /**
     * This method is used to get all trending Events with paging and
     * orderBy.
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

    public String getTrendingEvents(Integer viewerId, Integer limit, Integer offset, String direction, String orderBy)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;


        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;

        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findByIsTrendingAndEventUtcDatetimeGreaterThan(true, new Timestamp(System.currentTimeMillis()), pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());


        result = utils.constructSucessJSON(jObj);

        return result;


    }

    /**
     * This method is used to get all trending Events by status with
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

    public String getTrendingEvents(String status, Integer viewerId, Integer limit, Integer offset, String direction,
                                    String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;


        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findByStatusAndIsTrendingAndEventUtcDatetimeGreaterThan(status, true, new Timestamp(System.currentTimeMillis()), pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to update the Event.
     *
     * @param eventReqObj: Event
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String updateEvent(Event eventReqObj) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        String previousUrl = "";

        Optional<Event> eventEntity = null;
        try {
            eventEntity = eventRepository.findById(eventReqObj.getId());
            if (eventEntity == null) {
                if (eventReqObj.getEventImageUrl() != null) {
                    this.amazonClient.deleteFileFromS3Bucket(eventReqObj.getEventImageUrl());
                    LOGGER.info("Event is not present. deleting new uploaded file ::::::::: "
                            + eventReqObj.getEventImageUrl());
                }
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            } /*else if (eventReqObj.getEventImageUrl() != null) {
                previousUrl = eventEntity.get().getEventImageUrl();
            }*/

            if (eventReqObj.getStatus() != null && eventReqObj.getStatus().equalsIgnoreCase("Active")) {
                Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(eventEntity.get().getBroadcasterInfo().getId());

                if (broadcasterInfoEntity.get().getStatus().equalsIgnoreCase("InActive")) {
                    throw new InvalidParameterException("We cannot activate the event as broadcaster is Inactive.");
                }

            }

            if (eventReqObj.getStatus() != null && eventReqObj.getStatus().equalsIgnoreCase("InActive")) {
                /////TODO///////


            }

            Float commission = null;
            if (eventReqObj.getCommission() != null) {
                LOGGER.info("Event getCommission() :::::::::::::::::::: " + eventReqObj.getCommission());
                commission = eventReqObj.getCommission();
            }



            if(eventReqObj.getEventUtcDatetime() !=null) {

                /*
                TODO
                 */
            }

            eventHelper.populateEvent(eventReqObj, eventEntity.get());
            eventRepository.save(eventEntity.get());

            if (commission != null) {
                SpotlightCommission spotlightCommissionEntity = spotlightCommissionRepository
                        .findByEvent(eventEntity.get());

                SpotlightCommission spotlightCommissionReqObj = new SpotlightCommission();

                spotlightCommissionReqObj.setBroadcasterInfo(eventReqObj.getBroadcasterInfo());
                spotlightCommissionReqObj.setPercentage(commission);
                spotlightCommissionReqObj.setStatus(eventReqObj.getStatus());
                spotlightCommissionReqObj.setEvent(eventEntity.get());
                SpotlightCommission spotlightCommission = null;
                if (spotlightCommissionEntity != null)
                    spotlightCommission = spotlightCommissionHelper
                            .populateSpotlightCommission(spotlightCommissionReqObj, spotlightCommissionEntity);
                else
                    spotlightCommission = spotlightCommissionHelper
                            .populateSpotlightCommission(spotlightCommissionReqObj);
                spotlightCommissionRepository.save(spotlightCommission);
            }
           /* if (!previousUrl.equals("")) {
                this.amazonClient.deleteFileFromS3Bucket(previousUrl);
                LOGGER.info("Finally. deleting previous Event Image file ::::::::: " + previousUrl);
            }*/

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            if (eventEntity.get().getEventImageUrl() != null) {
                this.amazonClient.deleteFileFromS3Bucket(eventEntity.get().getEventImageUrl());
                LOGGER.info("ConstraintViolationException. deleting file ::::::::: "
                        + eventEntity.get().getEventImageUrl());
            }
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            if (eventEntity.get().getEventImageUrl() != null) {
                this.amazonClient.deleteFileFromS3Bucket(eventEntity.get().getEventImageUrl());
                LOGGER.info("HibernateException. deleting file ::::::::: "
                        + eventEntity.get().getEventImageUrl());
            }
            throw new Exception(sqlException.getMessage());
        }
        JSONObject jObj = new JSONObject();
        // jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(eventEntity.get(), null, null));
        result = utils.constructSucessJSON(jObj);
        return result;
    }


    /**
     * This method is used to review the Event.
     *
     * @param eventReview: EventReview
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String reviewEvent(EventReview eventReview) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        EventReview eventReviewEntity = null;

        try {
            EventReview eventReviewObj = eventReviewRepository.findOneByEventAndViewer(eventReview.getEvent(), eventReview.getViewer());
            if (eventReviewObj != null && eventReviewObj.getIsLike().booleanValue() == eventReview.getIsLike().booleanValue()) {
                throw new AlreadyExistException("Viewer has already reviewed the event.");
            } else if (eventReviewObj != null && eventReviewObj.getIsLike().booleanValue() != eventReview.getIsLike().booleanValue()) {
                eventReviewRepository.delete(eventReviewObj);
            }

            eventReviewEntity = eventReviewHelper.populateEventReview(eventReview);
            eventReviewEntity = eventReviewRepository.save(eventReviewEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTREVIEW, eventReviewHelper.buildResponseObject(eventReviewEntity));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get all Event Reviews by event id.
     *
     * @param eventId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getReviewsByEventId(Integer eventId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<EventReview> eventReviews = new ArrayList<EventReview>();

        try {
            Optional<Event> event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            eventReviews = eventReviewRepository.findByEvent(event.get());


        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        JSONArray eventReviewArr = eventReviewHelper.buildResponseObject(eventReviews);
        int likeCount = 0;
        int dislikeCount = 0;
        for (int i = 0; i < eventReviewArr.length(); i++) {
            JSONObject eventReviewObj = (JSONObject) eventReviewArr.get(i);
            boolean isLikeFlag = eventReviewObj.getBoolean("isLike");
            if (isLikeFlag) {
                likeCount++;
            } else {
                dislikeCount++;
            }

        }
        jObj.put(IConstants.EVENTREVIEWS, eventReviewArr);
        jObj.put("likeCount", likeCount);
        jObj.put("dislikeCount", dislikeCount);
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to delete the Event by id.
     *
     * @param eventId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String deleteEvent(Integer eventId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
        String result = null;
        Optional<Event> event = null;
        try {
            event = eventRepository.findById(eventId);
            if (!event.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            eventRepository.deleteById(eventId);
            if (event.get().getLiveStreamData() != null && !event.get().getLiveStreamData().isEmpty()) {
                WowzaClient wowzaClient = new WowzaClient(event.get().getLiveStreamConfig());
                if (event.get().getLiveStreamConfig().getConnectionType().equalsIgnoreCase("Cloud")) {
                    JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
                    if (liveStreamData.has("live_stream")) {
                        try {
                            JSONObject liveStreamJObj = new JSONObject(liveStreamData.get("live_stream").toString());
                            String liveStreamId = liveStreamJObj.get("id").toString();
                            wowzaClient.executeDelete("live_streams/" + liveStreamId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.error("Exception in deleting Event from Wowza  " + e.getMessage());
                            LOGGER.error("Exception in deleting Event from Wowza Cloud for EventId " + eventId);
                        }
                    }
                } else if (event.get().getLiveStreamConfig().getConnectionType().equalsIgnoreCase("Server")) {
                    try {
                        JSONObject liveStreamData = new JSONObject(event.get().getLiveStreamData());
                        String liveStreamId = liveStreamData.get("name").toString();
                        wowzaClient.executeDelete("applications/" + liveStreamId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("Exception in deleting Event from Wowza  " + e.getMessage());
                        LOGGER.error("Exception in deleting Event from Wowza Server for EventId " + eventId);
                    }
                }
            }
            this.amazonClient.deleteFileFromS3Bucket(event.get().getEventImageUrl());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new InvalidParameterException("We cannot delete the Event as there is associated data with the Event. Please deactivate it.");
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
        JSONObject jObj = new JSONObject();
        result = utils.constructSucessJSON(jObj);
        return result;
    }


//	public static void main(String[] args) {
//		JSONObject liveStreamData = new JSONObject("{\"live_stream\":{\"use_stream_source\":false,\"player_embed_code\":\"in_progress\",\"transcoder_type\":\"transcoded\",\"direct_playback_urls\":{\"rtmp\":[{\"name\":\"source\",\"url\":\"rtmp://bba78e-sandbox.entrypoint.cloud.wowza.com/app-6fb5/a4f150d8\"},{\"output_id\":\"93fwlb0l\",\"name\":\"passthrough\",\"url\":\"rtmp://bba78e-sandbox.entrypoint.cloud.wowza.com/app-6fb5/a4f150d8_stream1\"},{\"output_id\":\"mm690jmn\",\"name\":\"854x480\",\"url\":\"rtmp://bba78e-sandbox.entrypoint.cloud.wowza.com/app-6fb5/a4f150d8_stream2\"},{\"output_id\":\"frjvz6f9\",\"name\":\"640x360\",\"url\":\"rtmp://bba78e-sandbox.entrypoint.cloud.wowza.com/app-6fb5/a4f150d8_stream3\"},{\"output_id\":\"mvwdl7gj\",\"name\":\"512x288\",\"url\":\"rtmp://bba78e-sandbox.entrypoint.cloud.wowza.com/app-6fb5/a4f150d8_stream4\"},{\"output_id\":\"jbjr5dyp\",\"name\":\"320x180\",\"url\":\"rtmp://bba78e-sandbox.entrypoint.cloud.wowza.com/app-6fb5/a4f150d8_stream5\"}],\"rtsp\":[{\"name\":\"source\",\"url\":\"rtsp://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8\"},{\"output_id\":\"93fwlb0l\",\"name\":\"passthrough\",\"url\":\"rtsp://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream1\"},{\"output_id\":\"mm690jmn\",\"name\":\"854x480\",\"url\":\"rtsp://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream2\"},{\"output_id\":\"frjvz6f9\",\"name\":\"640x360\",\"url\":\"rtsp://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream3\"},{\"output_id\":\"mvwdl7gj\",\"name\":\"512x288\",\"url\":\"rtsp://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream4\"},{\"output_id\":\"jbjr5dyp\",\"name\":\"320x180\",\"url\":\"rtsp://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream5\"}],\"wowz\":[{\"name\":\"source\",\"url\":\"wowz://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8\"},{\"output_id\":\"93fwlb0l\",\"name\":\"passthrough\",\"url\":\"wowz://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream1\"},{\"output_id\":\"mm690jmn\",\"name\":\"854x480\",\"url\":\"wowz://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream2\"},{\"output_id\":\"frjvz6f9\",\"name\":\"640x360\",\"url\":\"wowz://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream3\"},{\"output_id\":\"mvwdl7gj\",\"name\":\"512x288\",\"url\":\"wowz://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream4\"},{\"output_id\":\"jbjr5dyp\",\"name\":\"320x180\",\"url\":\"wowz://bba78e-sandbox.entrypoint.cloud.wowza.com:1935/app-6fb5/a4f150d8_stream5\"}]},\"billing_mode\":\"pay_as_you_go\",\"broadcast_location\":\"us_west_california\",\"recording\":false,\"source_connection_information\":{\"disable_authentication\":false,\"password\":\"35784bc5\",\"application\":\"app-6fb5\",\"stream_name\":\"48fbf0ea\",\"host_port\":1935,\"primary_server\":\"bba78e-sandbox.entrypoint.cloud.wowza.com\",\"username\":\"client1229\"},\"created_at\":\"2018-12-27T11:12:41.000Z\",\"player_countdown_at\":\"2018-12-27T12:00:00.000Z\",\"player_responsive\":true,\"player_id\":\"xvsvnfkn\",\"aspect_ratio_width\":1280,\"player_hls_playback_url\":\"https://mock.wowzamockinjest-i.akamaihd.net/hls/live/62074933/148d4b12/playlist.m3u8\",\"updated_at\":\"2018-12-27T11:12:42.000Z\",\"delivery_method\":\"push\",\"links\":[{\"method\":\"GET\",\"rel\":\"self\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y\"},{\"method\":\"PATCH\",\"rel\":\"update\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y\"},{\"method\":\"GET\",\"rel\":\"state\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y/state\"},{\"method\":\"GET\",\"rel\":\"thumbnail_url\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y/thumbnail_url\"},{\"method\":\"PUT\",\"rel\":\"start\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y/start\"},{\"method\":\"PUT\",\"rel\":\"reset\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y/reset\"},{\"method\":\"PUT\",\"rel\":\"stop\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y/stop\"},{\"method\":\"PUT\",\"rel\":\"regenerate_connection_code\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y/regenerate_connection_code\"},{\"method\":\"DELETE\",\"rel\":\"delete\",\"href\":\"https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams/svt3jz1y\"}],\"id\":\"svt3jz1y\",\"delivery_protocols\":[\"rtmp\",\"rtsp\",\"wowz\",\"hls\"],\"closed_caption_type\":\"none\",\"connection_code_expires_at\":\"2018-12-28T11:12:41.000Z\",\"hosted_page_logo_image_url\":false,\"target_delivery_protocol\":\"hls-https\",\"hosted_page\":false,\"delivery_protocol\":\"hls-https\",\"encoder\":\"wowza_gocoder\",\"player_type\":\"original_html5\",\"stream_targets\":[{\"id\":\"xwwh5gyc\"}],\"player_countdown\":true,\"aspect_ratio_height\":720,\"name\":\"Event5\",\"connection_code\":\"0VuuUw\",\"low_latency\":false}}");
//		JSONObject liveStreamJObj= new JSONObject(liveStreamData.get("live_stream").toString());
//		System.out.println();
//		LOGGER.info("liveStreamJObj ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + liveStreamJObj.toString());
//		LOGGER.info("liveStreamJObj ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + liveStreamJObj.get("id").toString());
//	}

    /**
     * This method is used to get all Events by broadcaster with paging.
     *
     * @param status:        String
     * @param viewerId:      Integer
     * @param broadcasterId: Integer
     * @param limit:         Integer
     * @param offset:        Integer
     * @param direction:     String
     * @param orderBy:       String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEventsByBroadcaster(String status, Integer viewerId, Integer broadcasterId, Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;
        BroadcasterInfo broadcasterInfo = null;
        Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(broadcasterId);
        if (broadcasterInfoEntity.isPresent())
            broadcasterInfo = broadcasterInfoEntity.get();

        if (broadcasterInfo == null) {
            Optional<SpotlightUser> spotlightUser = spotlightUserRepository
                    .findById(broadcasterId);
            if (spotlightUser.isPresent()) {
                broadcasterInfo = broadcasterInfoRepository
                        .findBySpotlightUser(spotlightUser.get());
            }
        }
        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findByBroadcasterInfoAndStatus(broadcasterInfo, status, pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all the Events by viewerId with paging.
     *
     * @param status:    String
     * @param viewerId:  Integer
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String getAllViewerEvents(String status, Integer viewerId, Integer limit, Integer offset, String direction,
                                     String orderBy) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<Event> eventList = new ArrayList<Event>();
        int pageNum = offset / limit;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<Event> events = eventRepository.findAllByStatus(status, pageableRequest);
            totalCount = events.getTotalElements();
            List<Event> eventEntities = events.getContent();
            for (Event eventEntity : eventEntities) {
                Event eventDto = new Event();
                BeanUtils.copyProperties(eventEntity, eventDto);
                eventList.add(eventDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all Events by Broadcaster.
     *
     * @param status:        String
     * @param viewerId:      Integer
     * @param broadcasterId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getEventsByBroadcaster(String status, Integer viewerId, Integer broadcasterId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        BroadcasterInfo broadcasterInfo = null;
        Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(broadcasterId);
        if (broadcasterInfoEntity.isPresent())
            broadcasterInfo = broadcasterInfoEntity.get();

        if (broadcasterInfo == null) {
            Optional<SpotlightUser> spotlightUser = spotlightUserRepository
                    .findById(broadcasterId);
            if (spotlightUser.isPresent()) {
                broadcasterInfo = broadcasterInfoRepository
                        .findBySpotlightUser(spotlightUser.get());
            }
        }
        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            eventList = (List<Event>) eventRepository.findByBroadcasterInfoAndStatusAndEventUtcDatetimeGreaterThanOrderByEventUtcDatetimeDesc(broadcasterInfo, status,new Timestamp(System.currentTimeMillis()));

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all the Events by viewerId.
     *
     * @param status:   String
     * @param viewerId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String getAllViewerEvents(String status, Integer viewerId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
        String result = null;

        List<Event> eventList = null;
        Optional<Viewer> viewerEntity = null;
        try {
            if (viewerId != null)
                viewerEntity = viewerRepository.findById(viewerId);
            eventList = (List<Event>) eventRepository.findAllByStatus(status);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
//		if(eventList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, (viewerEntity != null && viewerEntity.isPresent()) ? viewerEntity.get() : null, null));

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

    @Override
    public String testDate(Event eventReqObj) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<Event> eventEntity = null;
        try {
            eventEntity = eventRepository.findById(eventReqObj.getId());
            eventHelper.populateEvent(eventReqObj, eventEntity.get());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        JSONObject jObj = new JSONObject();
        //jObj.put(IConstants.EVENT, eventHelper.buildResponseObject(eventEntity.get(), null, false));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

}
