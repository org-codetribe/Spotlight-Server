package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventType;
import com.yappyapps.spotlight.domain.Favorite;
import com.yappyapps.spotlight.domain.LiveStreamConfig;
import com.yappyapps.spotlight.domain.PricingRule;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.ViewerEvent;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.ICouponConsumptionRepository;
import com.yappyapps.spotlight.repository.IEventTypeRepository;
import com.yappyapps.spotlight.repository.IFavoriteRepository;
import com.yappyapps.spotlight.repository.ILiveStreamConfigRepository;
import com.yappyapps.spotlight.repository.IPricingRuleRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.IViewerEventRepository;
import com.yappyapps.spotlight.service.impl.PubNubService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The EventHelper class is the utility class to build and validate Event
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class EventHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventHelper.class);

	/*
	 * IEventTypeRepository Bean
	 */
	@Autowired
	private IEventTypeRepository eventTypeRepository;

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

	/*
	 * IViewerEventRepository Bean
	 */
	@Autowired
	private IViewerEventRepository viewerEventRepository;

	/*
	 * ISpotlightUserRepository Bean
	 */
	@Autowired
	private ISpotlightUserRepository spotlightUserRepository;

	/*
	 * ICouponConsumptionRepository Bean
	 */
	@Autowired
	private ICouponConsumptionRepository couponConsumptionRepository;

	/*
	 * IBroadcasterInfoRepository Bean
	 */
	@Autowired
	private IBroadcasterInfoRepository broadcasterInfoRepository;
	
	/**
	 * IFavoriteRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IFavoriteRepository favoriteRepository;

	/*
	 * EventTypeHelper Bean
	 */
	@Autowired
	private EventTypeHelper eventTypeHelper;

	/*
	 * BroadcasterInfoHelper Bean
	 */
	@Autowired
	private BroadcasterInfoHelper broadcasterInfoHelper;

	/*
	 * LiveStreamConfigHelper Bean
	 */
	@Autowired
	private LiveStreamConfigHelper liveStreamConfigHelper;

	/*
	 * PricingRuleHelper Bean
	 */
	@Autowired
	private PricingRuleHelper pricingRuleHelper;

	/**
	 * This method is used to create the Event Entity by copying properties from
	 * requested Bean
	 * 
	 * @param eventReqObj
	 *            : Event
	 * @return Event: eventEntity
	 * 
	 */
	public Event populateEvent(Event eventReqObj) {
		Event eventEntity = new Event();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		eventEntity.setActualPrice(eventReqObj.getActualPrice() != null ? eventReqObj.getActualPrice() : 0);
		eventEntity.setDisplayName(eventReqObj.getDisplayName() != null ? eventReqObj.getDisplayName() : null);
		eventEntity.setCreatedOn(currentTime);
		eventEntity.setStatus(eventReqObj.getStatus() != null ? eventReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		eventEntity.setTimezone(eventReqObj.getTimezone() != null ? eventReqObj.getTimezone() : null);

		eventEntity
				.setEventPreviewUrl(eventReqObj.getEventPreviewUrl() != null ? eventReqObj.getEventPreviewUrl() : null);
		eventEntity.setStreamName(eventReqObj.getStreamName() != null ? eventReqObj.getStreamName() : "default");
		eventEntity.setLiveStreamUrl(eventReqObj.getLiveStreamUrl() != null ? eventReqObj.getLiveStreamUrl() : "defaultURL");
		eventEntity.setDescription(eventReqObj.getDescription() != null ? eventReqObj.getDescription() : null);
		eventEntity.setEventImageUrl(eventReqObj.getEventImageUrl() != null ? eventReqObj.getEventImageUrl() : null);
		eventEntity.setEventVideoUrl(eventReqObj.getEventVideoUrl() != null ? eventReqObj.getEventVideoUrl() : null);
		eventEntity.setIsTrending(eventReqObj.getIsTrending() != null ? eventReqObj.getIsTrending() : false);
		eventEntity.setChatEnabled(eventReqObj.getChatEnabled() != null ? eventReqObj.getChatEnabled() : false);
		eventEntity.setEventUtcDatetime(eventReqObj.getEventUtcDatetime() != null ? eventReqObj.getEventUtcDatetime() : null);
		eventEntity.setUniqueName(Utils.generateRandomString(64));
		eventEntity.setTotalSeats(eventReqObj.getTotalSeats() != null ? eventReqObj.getTotalSeats() : null);
		eventEntity.setEventDuration(eventReqObj.getEventDuration() != null ? eventReqObj.getEventDuration() : null);
		eventEntity.setAddress1(eventReqObj.getAddress1() != null ? eventReqObj.getAddress1() : null);
		eventEntity.setAddress2(eventReqObj.getAddress2() != null ? eventReqObj.getAddress2() : null);
		eventEntity.setCity(eventReqObj.getCity() != null ? eventReqObj.getCity() : null);
		eventEntity.setCountry(eventReqObj.getCountry() != null ? eventReqObj.getCountry() : null);
		eventEntity.setState(eventReqObj.getState() != null ? eventReqObj.getState() : null);
		eventEntity.setZip(eventReqObj.getZip() != null ? eventReqObj.getZip() : null);
		eventEntity.setLiveStreamData(eventReqObj.getLiveStreamData() != null ? eventReqObj.getLiveStreamData() : null);
		

		if (eventReqObj.getBroadcasterInfo() != null) {
			Optional<BroadcasterInfo> broadcasterInfo = broadcasterInfoRepository
					.findById(eventReqObj.getBroadcasterInfo().getId());
			if (broadcasterInfo.isPresent())
				eventEntity.setBroadcasterInfo(broadcasterInfo.get());
		}

		if (eventEntity.getBroadcasterInfo() == null) {
			Optional<SpotlightUser> spotlightUser = spotlightUserRepository
					.findById(eventReqObj.getBroadcasterInfo().getId());
			if(spotlightUser.isPresent()) {
				BroadcasterInfo broadcasterInfo = broadcasterInfoRepository
						.findBySpotlightUser(spotlightUser.get());
				if (broadcasterInfo != null)
					eventEntity.setBroadcasterInfo(broadcasterInfo);

			}
		}

		if (eventReqObj.getLiveStreamConfig() != null) {
			Optional<LiveStreamConfig> liveStreamConfig = liveStreamConfigRepository
					.findById(eventReqObj.getLiveStreamConfig().getId());
			if (liveStreamConfig.isPresent())
				eventEntity.setLiveStreamConfig(liveStreamConfig.get());
		}

		if (eventReqObj.getPricingRule() != null) {
			Optional<PricingRule> pricingRule = pricingRuleRepository.findById(eventReqObj.getPricingRule().getId());
			if (pricingRule.isPresent())
				eventEntity.setPricingRule(pricingRule.get());
		}

		
		Set<EventType> eventTypeSet = new HashSet<EventType>();
		for (EventType eventType : eventReqObj.getEventType()) {
			Optional<EventType> eventTypeEntity = eventTypeRepository.findById(eventType.getId());
			if (eventTypeEntity.isPresent())
				eventTypeSet.add(eventTypeEntity.get());
		}
		eventEntity.setEventType(eventTypeSet);
		
		LOGGER.debug("Event populated from Requested Event Object ");
		return eventEntity;
	}

	/**
	 * This method is used to copy the Event properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param eventReqObj
	 *            : Event
	 * @param eventEntity
	 *            : Event
	 * @return Event: eventEntity
	 * 
	 */
	public Event populateEvent(Event eventReqObj, Event eventEntity) {
//		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		eventEntity.setActualPrice(
				eventReqObj.getActualPrice() != null ? eventReqObj.getActualPrice() : eventEntity.getActualPrice());
		
		eventEntity.setDisplayName(
				eventReqObj.getDisplayName() != null ? eventReqObj.getDisplayName() : eventEntity.getDisplayName());
		eventEntity.setStatus(eventReqObj.getStatus() != null ? eventReqObj.getStatus() : eventEntity.getStatus());
		eventEntity
				.setTimezone(eventReqObj.getTimezone() != null ? eventReqObj.getTimezone() : eventEntity.getTimezone());

		eventEntity.setEventPreviewUrl(eventReqObj.getEventPreviewUrl() != null ? eventReqObj.getEventPreviewUrl()
				: eventEntity.getEventPreviewUrl());
		eventEntity.setStreamName(
				eventReqObj.getStreamName() != null ? eventReqObj.getStreamName() : eventEntity.getStreamName());
		eventEntity.setLiveStreamUrl(eventReqObj.getLiveStreamUrl() != null ? eventReqObj.getLiveStreamUrl()
				: eventEntity.getLiveStreamUrl());
		eventEntity.setDescription(
				eventReqObj.getDescription() != null ? eventReqObj.getDescription() : eventEntity.getDescription());
		eventEntity.setEventImageUrl(eventReqObj.getEventImageUrl() != null ? eventReqObj.getEventImageUrl()
				: eventEntity.getEventImageUrl());
		eventEntity.setIsTrending(
				eventReqObj.getIsTrending() != null ? eventReqObj.getIsTrending() : eventEntity.getIsTrending());
		eventEntity.setChatEnabled(
				eventReqObj.getChatEnabled() != null ? eventReqObj.getChatEnabled() : eventEntity.getChatEnabled());
		eventEntity.setEventUtcDatetime(eventReqObj.getEventUtcDatetime() != null ? eventReqObj.getEventUtcDatetime() : eventEntity.getEventUtcDatetime());
		
		eventEntity.setTotalSeats(eventReqObj.getTotalSeats() != null ? eventReqObj.getTotalSeats() : eventEntity.getTotalSeats());
		eventEntity.setEventDuration(eventReqObj.getEventDuration() != null ? eventReqObj.getEventDuration() : eventEntity.getEventDuration());
		eventEntity.setAddress1(eventReqObj.getAddress1() != null ? eventReqObj.getAddress1() : eventEntity.getAddress1());
		eventEntity.setAddress2(eventReqObj.getAddress2() != null ? eventReqObj.getAddress2() : eventEntity.getAddress2());
		eventEntity.setCity(eventReqObj.getCity() != null ? eventReqObj.getCity() : eventEntity.getCity());
		eventEntity.setCountry(eventReqObj.getCountry() != null ? eventReqObj.getCountry() : eventEntity.getCountry());
		eventEntity.setState(eventReqObj.getState() != null ? eventReqObj.getState() : eventEntity.getState());
		eventEntity.setZip(eventReqObj.getZip() != null ? eventReqObj.getZip() : eventEntity.getZip());
		eventEntity.setLiveStreamData(eventReqObj.getLiveStreamData() != null ? eventReqObj.getLiveStreamData() : eventEntity.getLiveStreamData());

		if (eventReqObj.getBroadcasterInfo() != null) {
			Optional<BroadcasterInfo> broadcasterInfo = broadcasterInfoRepository
					.findById(eventReqObj.getBroadcasterInfo().getId());
			if (broadcasterInfo.isPresent())
				eventEntity.setBroadcasterInfo(broadcasterInfo.get());
		}

		if (eventReqObj.getLiveStreamConfig() != null) {
			Optional<LiveStreamConfig> liveStreamConfig = liveStreamConfigRepository
					.findById(eventReqObj.getLiveStreamConfig().getId());
			if (liveStreamConfig.isPresent())
				eventEntity.setLiveStreamConfig(liveStreamConfig.get());
		}

		if (eventReqObj.getPricingRule() != null) {
			Optional<PricingRule> pricingRule = pricingRuleRepository.findById(eventReqObj.getPricingRule().getId());
			if (pricingRule.isPresent())
				eventEntity.setPricingRule(pricingRule.get());
		}

		if (eventReqObj.getEventType().size() > 0) {
			Set<EventType> eventTypeSet = new HashSet<EventType>();
			for (EventType eventType : eventReqObj.getEventType()) {
				Optional<EventType> genreEntity = eventTypeRepository.findById(eventType.getId());
				eventTypeSet.add(genreEntity.get());
			}
			eventEntity.setEventType(eventTypeSet);
		}
		LOGGER.debug("Event Entity populated from Requested Event Object ");
		return eventEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param event:
	 *            Event
	 * @param viewer:
	 *            Viewer
	 * @param deepEventTypeFlag:
	 *            boolean
	 * @return JSONObject: eventObj
	 * 
	 */
	public JSONObject buildResponseObject(Event event, Viewer viewer, boolean deepEventTypeFlag,EventType eventType_) throws JSONException {
		JSONObject eventObj = new JSONObject();
		eventObj.put("id", event.getId());
		eventObj.put("actualPrice", event.getActualPrice());
		eventObj.put("createdOn", event.getCreatedOn());
		eventObj.put("displayName", event.getDisplayName());
		eventObj.put("status", event.getStatus());
		eventObj.put("timezone", event.getTimezone());
		eventObj.put("eventPreviewUrl", event.getEventPreviewUrl());
		eventObj.put("streamName", event.getStreamName());
		eventObj.put("liveStreamUrl", event.getLiveStreamUrl());
		eventObj.put("description", event.getDescription());
		eventObj.put("eventImageUrl", event.getEventImageUrl());
		eventObj.put("eventVideoUrl", event.getEventVideoUrl());
		eventObj.put("isTrending", event.getIsTrending());
		eventObj.put("chatEnabled", event.getChatEnabled());
		eventObj.put("eventUtcDatetime", event.getEventUtcDatetime());
		eventObj.put("uniqueName", event.getUniqueName());
		eventObj.put("totalSeats", event.getTotalSeats());
		eventObj.put("eventDuration", event.getEventDuration());
		eventObj.put("address1", event.getAddress1());
		eventObj.put("address2", event.getAddress2());
		eventObj.put("city", event.getCity());
		eventObj.put("country", event.getCountry());
		eventObj.put("state", event.getState());
		eventObj.put("zip", event.getZip());
		eventObj.put("liveStreamState", event.getLiveStreamState());
		
		
		
		Long soldSeats = viewerEventRepository.countByEvent(event);
		
		LOGGER.info("totalseats :::: " + event.getTotalSeats());
		LOGGER.info("soldSeats :::: " + soldSeats);
		
		eventObj.put("remainingSeats", event.getTotalSeats() - soldSeats);
		eventObj.put("soldSeats", soldSeats);
		
		if(event.getBroadcasterInfo() != null && event.getBroadcasterInfo().getId() != null) {
			Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(event.getBroadcasterInfo().getId());
			if(broadcasterInfoEntity.isPresent())
				eventObj.put("broadcasterInfo", broadcasterInfoHelper.buildResponseObject(broadcasterInfoEntity.get(), viewer, false));

			if(viewer != null && viewer.getId() != null) {
				Favorite favoriteEntity = favoriteRepository.findByEventAndViewer(event, viewer);
				if(favoriteEntity != null) {
					if (favoriteEntity.getEvent().getId() == event.getId()) {
						eventObj.put("isFavorite", true);
					} else {
						eventObj.put("isFavorite", false);
					}
				}
			}
		}
		
		LOGGER.info("viewer :::: " + viewer);
		
		if(viewer != null && viewer.getId() != null) {
			LOGGER.info("viewer :::: " + viewer + " :::: id " + viewer.getId());
			Boolean eventPurchased = viewerEventRepository.existsByEventAndViewer(event, viewer);
			LOGGER.info("eventPurchased :::: " + eventPurchased.booleanValue());
			if(eventPurchased) {
				eventObj.put("eventPurchased", true);
			} 
		}
		
		Long couponRedeemed = couponConsumptionRepository.countByEvent(event);
		eventObj.put("couponRedeemed", couponRedeemed);
		
		Float revenue = soldSeats * event.getActualPrice();
		eventObj.put("revenue", revenue);
		
		
		if(deepEventTypeFlag) {
			JSONObject liveStreamData = new JSONObject(event.getLiveStreamData());
			eventObj.put("liveStreamData", liveStreamData);
			
			JSONObject liveStreamDataMobile = new JSONObject();
			liveStreamDataMobile.put("hostAddress", "ec2-54-193-25-94.us-west-1.compute.amazonaws.com");
			liveStreamDataMobile.put("portNumber", 1935);
			liveStreamDataMobile.put("appName", "webrtc");
			liveStreamDataMobile.put("streamName", liveStreamData.has("streamName") ? liveStreamData.get("streamName") : "");
			eventObj.put("liveStreamDataMobile", liveStreamDataMobile);
		}
		
		if(deepEventTypeFlag && event.getLiveStreamConfig() != null && event.getLiveStreamConfig().getId() != null) {
//			eventObj.put("liveStreamConfig", new JSONObject().put("id",event.getLiveStreamConfig().getId()));
			Optional<LiveStreamConfig> liveStreamConfigEntity = liveStreamConfigRepository.findById(event.getLiveStreamConfig().getId());
			if(liveStreamConfigEntity.isPresent())
				eventObj.put("liveStreamConfig", liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntity.get()));
		}
		
		if(event.getPricingRule() != null && event.getPricingRule().getId() != null) {
//			eventObj.put("pricingRule", new JSONObject().put("id", event.getPricingRule().getId()));
			Optional<PricingRule> pricingRuleEntity = pricingRuleRepository.findById(event.getPricingRule().getId());
			if(pricingRuleEntity.isPresent())
				eventObj.put("pricingRule", pricingRuleHelper.buildResponseObject(pricingRuleEntity.get()));
		}
		
		
		
		if (deepEventTypeFlag) {
			JSONArray eventTypeArr = new JSONArray();
			for (EventType eventType : event.getEventType()) {
				eventTypeArr.put(eventTypeHelper.buildResponseObject(eventType));
			}
			eventObj.put(IConstants.EVENTTYPES, eventTypeArr);
		}
		
		LOGGER.debug("Event Response Object built for Event Object id :::: " + event.getId());
		return eventObj;
	}

	public JSONObject buildResponseObject(Event event, Viewer viewer, boolean deepEventTypeFlag, SpotlightUser spotlightUser) throws JSONException {
		JSONObject eventObj = new JSONObject();
		eventObj.put("id", event.getId());
		eventObj.put("actualPrice", event.getActualPrice());
		eventObj.put("createdOn", event.getCreatedOn());
		eventObj.put("displayName", event.getDisplayName());
		eventObj.put("status", event.getStatus());
		eventObj.put("timezone", event.getTimezone());
		eventObj.put("eventPreviewUrl", event.getEventPreviewUrl());
		eventObj.put("streamName", event.getStreamName());
		eventObj.put("liveStreamUrl", event.getLiveStreamUrl());
		eventObj.put("description", event.getDescription());
		eventObj.put("eventImageUrl", event.getEventImageUrl());
		eventObj.put("eventVideoUrl", event.getEventVideoUrl());
		eventObj.put("isTrending", event.getIsTrending());
		eventObj.put("chatEnabled", event.getChatEnabled());
		eventObj.put("eventUtcDatetime", event.getEventUtcDatetime());
		eventObj.put("uniqueName", event.getUniqueName());
		eventObj.put("totalSeats", event.getTotalSeats());
		eventObj.put("eventDuration", event.getEventDuration());
		eventObj.put("address1", event.getAddress1());
		eventObj.put("address2", event.getAddress2());
		eventObj.put("city", event.getCity());
		eventObj.put("country", event.getCountry());
		eventObj.put("state", event.getState());
		eventObj.put("zip", event.getZip());
		eventObj.put("liveStreamState", event.getLiveStreamState());
		
		
		
		Long soldSeats = viewerEventRepository.countByEvent(event);
		
		LOGGER.info("totalseats :::: " + event.getTotalSeats());
		LOGGER.info("soldSeats :::: " + soldSeats);
		
		eventObj.put("remainingSeats", event.getTotalSeats() - soldSeats);
		eventObj.put("soldSeats", soldSeats);
		
		if(event.getBroadcasterInfo() != null && event.getBroadcasterInfo().getId() != null) {
			Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(event.getBroadcasterInfo().getId());
			if(broadcasterInfoEntity.isPresent())
				eventObj.put("broadcasterInfo", broadcasterInfoHelper.buildResponseObject(broadcasterInfoEntity.get(), viewer, false));

			if(viewer != null && viewer.getId() != null) {
				Favorite favoriteEntity = favoriteRepository.findByBroadcasterInfoAndEventAndViewer(broadcasterInfoEntity.get(), event, viewer);
				if(favoriteEntity != null) {
					if(favoriteEntity.getEvent().getId() == event.getId()) {
						eventObj.put("isFavorite", true);
					} else {
						eventObj.put("isFavorite", false);
					}
				}
			}
		}
		
		LOGGER.info("viewer :::: " + viewer);
		
		if(viewer != null && viewer.getId() != null) {
			LOGGER.info("viewer :::: " + viewer + " :::: id " + viewer.getId());
			Boolean eventPurchased = viewerEventRepository.existsByEventAndViewer(event, viewer);
			LOGGER.info("eventPurchased :::: " + eventPurchased.booleanValue());
			if(eventPurchased) {
				eventObj.put("eventPurchased", true);
			} 
		}
		
		if(spotlightUser != null) {
			LOGGER.info("spotlightUser :::: " + spotlightUser + " :::: id " + spotlightUser.getId());
			eventObj.put("adminChatAuthKey", event.getAdminChatAuthKey());
			eventObj.put("authKey", event.getAdminChatAuthKey());
			LOGGER.info("PubNubService.PUBLISH_KEY :::: " + PubNubService.PUBLISH_KEY);
			eventObj.put("publishKey", PubNubService.PUBLISH_KEY);
			LOGGER.info("PubNubService.SUBSCRIBE_KEY :::: " + PubNubService.SUBSCRIBE_KEY);
			eventObj.put("subscribeKey", PubNubService.SUBSCRIBE_KEY);

//			eventObj.put("broadcasterChatAuthKey", event.getBroadcasterChatAuthKey());
		}
		
		Long couponRedeemed = couponConsumptionRepository.countByEvent(event);
		eventObj.put("couponRedeemed", couponRedeemed);
		
		Float revenue = soldSeats * event.getActualPrice();
		eventObj.put("revenue", revenue);
		
		
		if(deepEventTypeFlag) {
			JSONObject liveStreamData = new JSONObject(event.getLiveStreamData());
			eventObj.put("liveStreamData", liveStreamData);
			
			JSONObject liveStreamDataMobile = new JSONObject();
			liveStreamDataMobile.put("hostAddress", "ec2-54-193-25-94.us-west-1.compute.amazonaws.com");
			liveStreamDataMobile.put("portNumber", 1935);
			liveStreamDataMobile.put("appName", "webrtc");
			liveStreamDataMobile.put("streamName", liveStreamData.has("streamName") ? liveStreamData.get("streamName") : "");
			eventObj.put("liveStreamDataMobile", liveStreamDataMobile);
		}
		
		if(deepEventTypeFlag && event.getLiveStreamConfig() != null && event.getLiveStreamConfig().getId() != null) {
//			eventObj.put("liveStreamConfig", new JSONObject().put("id",event.getLiveStreamConfig().getId()));
			Optional<LiveStreamConfig> liveStreamConfigEntity = liveStreamConfigRepository.findById(event.getLiveStreamConfig().getId());
			if(liveStreamConfigEntity.isPresent())
				eventObj.put("liveStreamConfig", liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntity.get()));
		}
		
		if(event.getPricingRule() != null && event.getPricingRule().getId() != null) {
//			eventObj.put("pricingRule", new JSONObject().put("id", event.getPricingRule().getId()));
			Optional<PricingRule> pricingRuleEntity = pricingRuleRepository.findById(event.getPricingRule().getId());
			if(pricingRuleEntity.isPresent())
				eventObj.put("pricingRule", pricingRuleHelper.buildResponseObject(pricingRuleEntity.get()));
		}
		
		
		
		if (deepEventTypeFlag) {
			JSONArray eventTypeArr = new JSONArray();
			for (EventType eventType : event.getEventType()) {
				eventTypeArr.put(eventTypeHelper.buildResponseObject(eventType));
			}
			eventObj.put(IConstants.EVENTTYPES, eventTypeArr);
		}
		
		LOGGER.debug("Event Response Object built for Event Object id :::: " + event.getId());
		return eventObj;
	}

	public JSONObject buildResponseObject(Event event, Viewer viewer, boolean deepEventTypeFlag, BroadcasterInfo broadcasterInfo) throws JSONException {
		JSONObject eventObj = new JSONObject();
		eventObj.put("id", event.getId());
		eventObj.put("actualPrice", event.getActualPrice());
		eventObj.put("createdOn", event.getCreatedOn());
		eventObj.put("displayName", event.getDisplayName());
		eventObj.put("status", event.getStatus());
		eventObj.put("timezone", event.getTimezone());
		eventObj.put("eventPreviewUrl", event.getEventPreviewUrl());
		eventObj.put("streamName", event.getStreamName());
		eventObj.put("liveStreamUrl", event.getLiveStreamUrl());
		eventObj.put("description", event.getDescription());
		eventObj.put("eventImageUrl", event.getEventImageUrl());
		eventObj.put("eventVideoUrl", event.getEventVideoUrl());
		eventObj.put("isTrending", event.getIsTrending());
		eventObj.put("chatEnabled", event.getChatEnabled());
		eventObj.put("eventUtcDatetime", event.getEventUtcDatetime());
		eventObj.put("uniqueName", event.getUniqueName());
		eventObj.put("totalSeats", event.getTotalSeats());
		eventObj.put("eventDuration", event.getEventDuration());
		eventObj.put("address1", event.getAddress1());
		eventObj.put("address2", event.getAddress2());
		eventObj.put("city", event.getCity());
		eventObj.put("country", event.getCountry());
		eventObj.put("state", event.getState());
		eventObj.put("zip", event.getZip());
		eventObj.put("liveStreamState", event.getLiveStreamState());
		
		
		
		Long soldSeats = viewerEventRepository.countByEvent(event);
		
		LOGGER.info("totalseats :::: " + event.getTotalSeats());
		LOGGER.info("soldSeats :::: " + soldSeats);
		
		eventObj.put("remainingSeats", event.getTotalSeats() - soldSeats);
		eventObj.put("soldSeats", soldSeats);
		
		if(event.getBroadcasterInfo() != null && event.getBroadcasterInfo().getId() != null) {
			Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(event.getBroadcasterInfo().getId());
			if(broadcasterInfoEntity.isPresent())
				eventObj.put("broadcasterInfo", broadcasterInfoHelper.buildResponseObject(broadcasterInfoEntity.get(), viewer, false));

			if(viewer != null && viewer.getId() != null) {
				Favorite favoriteEntity = favoriteRepository.findByBroadcasterInfoAndEventAndViewer(broadcasterInfoEntity.get(), event, viewer);
				if(favoriteEntity != null) {
					if(favoriteEntity.getEvent().getId() == event.getId()) {
						eventObj.put("isFavorite", true);
					} else {
						eventObj.put("isFavorite", false);
					}
				}
			}
		}
		
		LOGGER.info("viewer :::: " + viewer);
		
		if(viewer != null && viewer.getId() != null) {
			LOGGER.info("viewer :::: " + viewer + " :::: id " + viewer.getId());
			Boolean eventPurchased = viewerEventRepository.existsByEventAndViewer(event, viewer);
			LOGGER.info("eventPurchased :::: " + eventPurchased.booleanValue());
			if(eventPurchased) {
				eventObj.put("eventPurchased", true);
			} 
		}
		
		if(broadcasterInfo != null) {
			LOGGER.info("broadcasterInfo :::: " + broadcasterInfo + " :::: id " + broadcasterInfo.getId());
//			eventObj.put("adminChatAuthKey", event.getAdminChatAuthKey());
			eventObj.put("broadcasterChatAuthKey", event.getBroadcasterChatAuthKey());
			eventObj.put("authKey", event.getBroadcasterChatAuthKey());
			LOGGER.info("PubNubService.PUBLISH_KEY :::: " + PubNubService.PUBLISH_KEY);
			eventObj.put("publishKey", PubNubService.PUBLISH_KEY);
			LOGGER.info("PubNubService.SUBSCRIBE_KEY :::: " + PubNubService.SUBSCRIBE_KEY);
			eventObj.put("subscribeKey", PubNubService.SUBSCRIBE_KEY);

		}
		
		Long couponRedeemed = couponConsumptionRepository.countByEvent(event);
		eventObj.put("couponRedeemed", couponRedeemed);
		
		Float revenue = soldSeats * event.getActualPrice();
		eventObj.put("revenue", revenue);
		
		
		if(deepEventTypeFlag) {
			JSONObject liveStreamData = new JSONObject(event.getLiveStreamData());
			eventObj.put("liveStreamData", liveStreamData);
			
			JSONObject liveStreamDataMobile = new JSONObject();
			liveStreamDataMobile.put("hostAddress", "ec2-54-193-25-94.us-west-1.compute.amazonaws.com");
			liveStreamDataMobile.put("portNumber", 1935);
			liveStreamDataMobile.put("appName", "webrtc");
			liveStreamDataMobile.put("streamName", liveStreamData.has("streamName") ? liveStreamData.get("streamName") : "");
			eventObj.put("liveStreamDataMobile", liveStreamDataMobile);
		}
		
		if(deepEventTypeFlag && event.getLiveStreamConfig() != null && event.getLiveStreamConfig().getId() != null) {
//			eventObj.put("liveStreamConfig", new JSONObject().put("id",event.getLiveStreamConfig().getId()));
			Optional<LiveStreamConfig> liveStreamConfigEntity = liveStreamConfigRepository.findById(event.getLiveStreamConfig().getId());
			if(liveStreamConfigEntity.isPresent())
				eventObj.put("liveStreamConfig", liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntity.get()));
		}
		
		if(event.getPricingRule() != null && event.getPricingRule().getId() != null) {
//			eventObj.put("pricingRule", new JSONObject().put("id", event.getPricingRule().getId()));
			Optional<PricingRule> pricingRuleEntity = pricingRuleRepository.findById(event.getPricingRule().getId());
			if(pricingRuleEntity.isPresent())
				eventObj.put("pricingRule", pricingRuleHelper.buildResponseObject(pricingRuleEntity.get()));
		}
		
		
		
		if (deepEventTypeFlag) {
			JSONArray eventTypeArr = new JSONArray();
			for (EventType eventType : event.getEventType()) {
				eventTypeArr.put(eventTypeHelper.buildResponseObject(eventType));
			}
			eventObj.put(IConstants.EVENTTYPES, eventTypeArr);
		}
		
		LOGGER.debug("Event Response Object built for Event Object id :::: " + event.getId());
		return eventObj;
	}

	public JSONObject buildResponseObject(Event event, Viewer viewer, boolean deepEventTypeFlag, Viewer viewer1) throws JSONException {
		JSONObject eventObj = new JSONObject();
		eventObj.put("id", event.getId());
		eventObj.put("actualPrice", event.getActualPrice());
		eventObj.put("createdOn", event.getCreatedOn());
		eventObj.put("displayName", event.getDisplayName());
		eventObj.put("status", event.getStatus());
		eventObj.put("timezone", event.getTimezone());
		eventObj.put("eventPreviewUrl", event.getEventPreviewUrl());
		eventObj.put("streamName", event.getStreamName());
		eventObj.put("liveStreamUrl", event.getLiveStreamUrl());
		eventObj.put("description", event.getDescription());
		eventObj.put("eventImageUrl", event.getEventImageUrl());
		eventObj.put("eventVideoUrl", event.getEventVideoUrl());
		eventObj.put("isTrending", event.getIsTrending());
		eventObj.put("chatEnabled", event.getChatEnabled());
		eventObj.put("eventUtcDatetime", event.getEventUtcDatetime());
		eventObj.put("uniqueName", event.getUniqueName());
		eventObj.put("totalSeats", event.getTotalSeats());
		eventObj.put("eventDuration", event.getEventDuration());
		eventObj.put("address1", event.getAddress1());
		eventObj.put("address2", event.getAddress2());
		eventObj.put("city", event.getCity());
		eventObj.put("country", event.getCountry());
		eventObj.put("state", event.getState());
		eventObj.put("zip", event.getZip());
		eventObj.put("liveStreamState", event.getLiveStreamState());
		
		
		
		Long soldSeats = viewerEventRepository.countByEvent(event);
		
		LOGGER.info("totalseats :::: " + event.getTotalSeats());
		LOGGER.info("soldSeats :::: " + soldSeats);
		
		eventObj.put("remainingSeats", event.getTotalSeats() - soldSeats);
		eventObj.put("soldSeats", soldSeats);
		
		if(event.getBroadcasterInfo() != null && event.getBroadcasterInfo().getId() != null) {
			Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(event.getBroadcasterInfo().getId());
			if(broadcasterInfoEntity.isPresent())
				eventObj.put("broadcasterInfo", broadcasterInfoHelper.buildResponseObject(broadcasterInfoEntity.get(), viewer, false));

			if(viewer != null && viewer.getId() != null) {
				Favorite favoriteEntity = favoriteRepository.findByBroadcasterInfoAndEventAndViewer(broadcasterInfoEntity.get(), event, viewer);
				if(favoriteEntity != null) {
					if(favoriteEntity.getEvent().getId() == event.getId()) {
						eventObj.put("isFavorite", true);
					} else {
						eventObj.put("isFavorite", false);
					}
				}
			}
		}
		
		LOGGER.info("viewer :::: " + viewer);
		
		if(viewer != null && viewer.getId() != null) {
			LOGGER.info("viewer :::: " + viewer + " :::: id " + viewer.getId());
			Boolean eventPurchased = viewerEventRepository.existsByEventAndViewer(event, viewer);
			LOGGER.info("eventPurchased :::: " + eventPurchased.booleanValue());
			if(eventPurchased) {
				eventObj.put("eventPurchased", true);
			} 
		}
		
		if(viewer1 != null) {
			LOGGER.info("viewer :::: " + viewer1 + " :::: id " + viewer1.getId());
			List<ViewerEvent> viewerEvents = viewerEventRepository.findByEventAndViewer(event, viewer1);
			if(viewerEvents != null && viewerEvents.size() > 0) {
				LOGGER.info("viewerChatAuthKey :::: " + viewerEvents.get(0).getViewerChatAuthKey());
				eventObj.put("viewerChatAuthKey", viewerEvents.get(0).getViewerChatAuthKey());
				eventObj.put("authKey", viewerEvents.get(0).getViewerChatAuthKey());
			} else {
				eventObj.put("viewerChatAuthKey", "");
				eventObj.put("authKey", "");
			}
			LOGGER.info("PubNubService.PUBLISH_KEY :::: " + PubNubService.PUBLISH_KEY);
			eventObj.put("publishKey", PubNubService.PUBLISH_KEY);
			LOGGER.info("PubNubService.SUBSCRIBE_KEY :::: " + PubNubService.SUBSCRIBE_KEY);
			eventObj.put("subscribeKey", PubNubService.SUBSCRIBE_KEY);
		}
		
		Long couponRedeemed = couponConsumptionRepository.countByEvent(event);
		eventObj.put("couponRedeemed", couponRedeemed);
		
		Float revenue = soldSeats * event.getActualPrice();
		eventObj.put("revenue", revenue);
		
		
		if(deepEventTypeFlag) {
			JSONObject liveStreamData = new JSONObject(event.getLiveStreamData());
			eventObj.put("liveStreamData", liveStreamData);
			
			JSONObject liveStreamDataMobile = new JSONObject();
			liveStreamDataMobile.put("hostAddress", "ec2-54-193-25-94.us-west-1.compute.amazonaws.com");
			liveStreamDataMobile.put("portNumber", 1935);
			liveStreamDataMobile.put("appName", "webrtc");
			liveStreamDataMobile.put("streamName", liveStreamData.has("streamName") ? liveStreamData.get("streamName") : "");
			eventObj.put("liveStreamDataMobile", liveStreamDataMobile);
		}
		
		if(deepEventTypeFlag && event.getLiveStreamConfig() != null && event.getLiveStreamConfig().getId() != null) {
//			eventObj.put("liveStreamConfig", new JSONObject().put("id",event.getLiveStreamConfig().getId()));
			Optional<LiveStreamConfig> liveStreamConfigEntity = liveStreamConfigRepository.findById(event.getLiveStreamConfig().getId());
			if(liveStreamConfigEntity.isPresent())
				eventObj.put("liveStreamConfig", liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntity.get()));
		}
		
		if(event.getPricingRule() != null && event.getPricingRule().getId() != null) {
//			eventObj.put("pricingRule", new JSONObject().put("id", event.getPricingRule().getId()));
			Optional<PricingRule> pricingRuleEntity = pricingRuleRepository.findById(event.getPricingRule().getId());
			if(pricingRuleEntity.isPresent())
				eventObj.put("pricingRule", pricingRuleHelper.buildResponseObject(pricingRuleEntity.get()));
		}
		
		
		
		if (deepEventTypeFlag) {
			JSONArray eventTypeArr = new JSONArray();
			for (EventType eventType : event.getEventType()) {
				eventTypeArr.put(eventTypeHelper.buildResponseObject(eventType));
			}
			eventObj.put(IConstants.EVENTTYPES, eventTypeArr);
		}
		
		LOGGER.debug("Event Response Object built for Event Object id :::: " + event.getId());
		return eventObj;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param eventList
	 *            : List&lt;Event&gt;
	 * @param viewer:
	 *            Viewer
	 * @return JSONArray: eventArr
	 * 
	 */
	public JSONArray buildResponseObject(List<Event> eventList, Viewer viewer,EventType eventType) throws JSONException {
		JSONArray eventArr = new JSONArray();
		for (Event event : eventList) {
			JSONObject eventObj = buildResponseObject(event, viewer, true,eventType);
			if (eventObj != null)
				eventArr.put(eventObj);

		}
		LOGGER.debug("Event Response Array built with size :::: " + eventArr.length());
		return eventArr;
	}
}
