package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.TermVector;
import org.springframework.data.annotation.Transient;

/**
 * The Event class is the domain class which maps the Event java object to event
 * table.
 * 
 * <h1>@Entity</h1> will enable it to map to database table.
 * 
 * <h1>@Table</h1> provides the database table name
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Indexed
@Entity
@Table(name = "EVENT")
public class Event {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Field(termVector = TermVector.YES)
	@Column(name = "DISPLAY_NAME", length = 255)
	@NotNull
	private String displayName;

	@ManyToOne
	@JoinColumn(name = "broadcaster_info_id", nullable = false)
	private BroadcasterInfo broadcasterInfo = null;

	@Field(termVector = TermVector.YES)
	@Column(name = "DESCRIPTION", columnDefinition = "Text")
	@NotNull
	private String description;

	@Column(name = "LIVE_STREAM_URL", columnDefinition = "Text")
	@NotNull
	private String liveStreamUrl;

	@Column(name = "event_UTC_Datetime", columnDefinition = "TimeStamp DEFAULT CURRENT_TIMESTAMP")
	@NotNull
	private Timestamp eventUtcDatetime;

	@Column(name = "TIMEZONE", length = 100)
	@NotNull
	private String timezone;

	@ManyToOne
	@JoinColumn(name = "live_stream_config_id", nullable = false)
	private LiveStreamConfig liveStreamConfig = null;

	@ManyToOne
	@JoinColumn(name = "pricing_rule_id", nullable = false)
	private PricingRule pricingRule = null;

	@Column(name = "event_image_url", columnDefinition = "Text")
	@NotNull
	private String eventImageUrl;

	@Column(name = "IS_TRENDING", length = 1)
	@NotNull
	private Boolean isTrending;

	@Column(name = "STREAM_NAME", length = 255)
	@NotNull
	private String streamName;

	@Column(name = "event_preview_url", columnDefinition = "Text")
	private String eventPreviewUrl;

	@Column(name = "CHAT_ENABLED")
	@NotNull
	private Boolean chatEnabled;

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "ACTUAL_PRICE", columnDefinition = "Float")
	@NotNull
	private Float actualPrice;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

	@Column(name = "UNIQUE_NAME", length = 255, unique = true)
	@NotNull
	private String uniqueName;

	@Column(name = "TOTAL_SEATS")
	private Integer totalSeats;

	@Column(name = "EVENT_DURATION", nullable = false)
	private Integer eventDuration;

	@Field(termVector = TermVector.YES)
	@Column(name = "ADDRESS1", length = 255)
	private String address1;

	@Field(termVector = TermVector.YES)
	@Column(name = "ADDRESS2", length = 255)
	private String address2;

	@Field(termVector = TermVector.YES)
	@Column(name = "CITY", length = 100)
	private String city;

	@Field(termVector = TermVector.YES)
	@Column(name = "STATE", length = 100)
	private String state;

	@Field(termVector = TermVector.YES)
	@Column(name = "ZIP", length = 50)
	private String zip;

	@Field(termVector = TermVector.YES)
	@Column(name = "COUNTRY", length = 100)
	private String country;

	@Column(name = "show_to_public")
	private Integer showToPublic;


	@Column(name = "admin_chat_auth_key")
	@NotNull
	private String adminChatAuthKey;

	@Column(name = "broadcaster_chat_auth_key")
	@NotNull
	private String broadcasterChatAuthKey;

	@Column(name = "LIVE_STREAM_DATA", columnDefinition = "Text")
	@NotNull
	private String liveStreamData;

	@Transient
	private transient Float commission;
	
	@Transient
	private transient String liveStreamState = "stopped";
	
	@IndexedEmbedded
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "EVENT_TYPE_EVENT", joinColumns = {
			@JoinColumn(name = "EVENT_ID") }, inverseJoinColumns = { @JoinColumn(name = "EVENT_TYPE_ID") })
	private Set<EventType> eventType = new HashSet<EventType>(0);
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the eventType
	 */
	public Set<EventType> getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(Set<EventType> eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the broadcasterInfo
	 */
	public BroadcasterInfo getBroadcasterInfo() {
		return broadcasterInfo;
	}

	/**
	 * @param broadcasterInfo
	 *            the broadcasterInfo to set
	 */
	public void setBroadcasterInfo(BroadcasterInfo broadcasterInfo) {
		this.broadcasterInfo = broadcasterInfo;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the liveStreamUrl
	 */
	public String getLiveStreamUrl() {
		return liveStreamUrl;
	}

	/**
	 * @param liveStreamUrl
	 *            the liveStreamUrl to set
	 */
	public void setLiveStreamUrl(String liveStreamUrl) {
		this.liveStreamUrl = liveStreamUrl;
	}

	/**
	 * @return the eventUtcDatetime
	 */
	public Timestamp getEventUtcDatetime() {
		return eventUtcDatetime;
	}

	/**
	 * @param eventUtcDatetime
	 *            the eventUtcDatetime to set
	 */
	public void setEventUtcDatetime(Timestamp eventUtcDatetime) {
		this.eventUtcDatetime = eventUtcDatetime;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone
	 *            the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the liveStreamConfig
	 */
	public LiveStreamConfig getLiveStreamConfig() {
		return liveStreamConfig;
	}

	/**
	 * @param liveStreamConfig
	 *            the liveStreamConfig to set
	 */
	public void setLiveStreamConfig(LiveStreamConfig liveStreamConfig) {
		this.liveStreamConfig = liveStreamConfig;
	}

	/**
	 * @return the pricingRule
	 */
	public PricingRule getPricingRule() {
		return pricingRule;
	}

	/**
	 * @param pricingRule
	 *            the pricingRule to set
	 */
	public void setPricingRule(PricingRule pricingRule) {
		this.pricingRule = pricingRule;
	}

	/**
	 * @return the eventImageUrl
	 */
	public String getEventImageUrl() {
		return eventImageUrl;
	}

	/**
	 * @param eventImageUrl
	 *            the eventImageUrl to set
	 */
	public void setEventImageUrl(String eventImageUrl) {
		this.eventImageUrl = eventImageUrl;
	}

	/**
	 * @return the isTrending
	 */
	public Boolean getIsTrending() {
		return isTrending;
	}

	/**
	 * @param isTrending
	 *            the isTrending to set
	 */
	public void setIsTrending(Boolean isTrending) {
		this.isTrending = isTrending;
	}

	/**
	 * @return the streamName
	 */
	public String getStreamName() {
		return streamName;
	}

	/**
	 * @param streamName
	 *            the streamName to set
	 */
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	/**
	 * @return the eventPreviewUrl
	 */
	public String getEventPreviewUrl() {
		return eventPreviewUrl;
	}

	/**
	 * @param eventPreviewUrl
	 *            the eventPreviewUrl to set
	 */
	public void setEventPreviewUrl(String eventPreviewUrl) {
		this.eventPreviewUrl = eventPreviewUrl;
	}

	/**
	 * @return the chatEnabled
	 */
	public Boolean getChatEnabled() {
		return chatEnabled;
	}

	/**
	 * @param chatEnabled
	 *            the chatEnabled to set
	 */
	public void setChatEnabled(Boolean chatEnabled) {
		this.chatEnabled = chatEnabled;
	}

	/**
	 * @return the createdOn
	 */
	public Timestamp getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the actualPrice
	 */
	public Float getActualPrice() {
		return actualPrice;
	}

	/**
	 * @param actualPrice
	 *            the actualPrice to set
	 */
	public void setActualPrice(Float actualPrice) {
		this.actualPrice = actualPrice;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the uniqueName
	 */
	public String getUniqueName() {
		return uniqueName;
	}

	/**
	 * @param uniqueName
	 *            the uniqueName to set
	 */
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	
	/**
	 * @return the totalSeats
	 */
	public Integer getTotalSeats() {
		return totalSeats;
	}

	/**
	 * @param totalSeats the totalSeats to set
	 */
	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	/**
	 * @return the eventDuration
	 */
	public Integer getEventDuration() {
		return eventDuration;
	}

	/**
	 * @param eventDuration the eventDuration to set
	 */
	public void setEventDuration(Integer eventDuration) {
		this.eventDuration = eventDuration;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the commission
	 */
	public Float getCommission() {
		return commission;
	}

	/**
	 * @param commission
	 *            the commission to set
	 */
	public void setCommission(Float commission) {
		this.commission = commission;
	}

	/**
	 * @return the liveStreamData
	 */
	public String getLiveStreamData() {
		return liveStreamData;
	}

	/**
	 * @param liveStreamData the liveStreamData to set
	 */
	public void setLiveStreamData(String liveStreamData) {
		this.liveStreamData = liveStreamData;
	}

	/**
	 * @return the liveStreamState
	 */
	public String getLiveStreamState() {
		return liveStreamState;
	}

	/**
	 * @param liveStreamState the liveStreamState to set
	 */
	public void setLiveStreamState(String liveStreamState) {
		this.liveStreamState = liveStreamState;
	}

	/**
	 * @return the adminChatAuthKey
	 */
	public String getAdminChatAuthKey() {
		return adminChatAuthKey;
	}

	/**
	 * @param adminChatAuthKey the adminChatAuthKey to set
	 */
	public void setAdminChatAuthKey(String adminChatAuthKey) {
		this.adminChatAuthKey = adminChatAuthKey;
	}

	/**
	 * @return the broadcasterChatAuthKey
	 */
	public String getBroadcasterChatAuthKey() {
		return broadcasterChatAuthKey;
	}

	/**
	 * @param broadcasterChatAuthKey the broadcasterChatAuthKey to set
	 */
	public void setBroadcasterChatAuthKey(String broadcasterChatAuthKey) {
		this.broadcasterChatAuthKey = broadcasterChatAuthKey;
	}

	public Boolean getTrending() {
		return isTrending;
	}

	public void setTrending(Boolean trending) {
		isTrending = trending;
	}

	public Integer getShowToPublic() {
		return showToPublic;
	}

	public void setShowToPublic(Integer showToPublic) {
		this.showToPublic = showToPublic;
	}
}
