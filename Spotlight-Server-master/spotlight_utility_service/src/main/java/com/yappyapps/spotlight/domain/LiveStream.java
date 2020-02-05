package com.yappyapps.spotlight.domain;

import java.util.Date;

import org.json.JSONObject;

public class LiveStream {

	private String uri = "";
	private String connectionType = "Cloud";
	private Integer aspectRatioHeight = 720;
	private Integer aspectRatioWidth = 1280;
	private String billingMode = "pay_as_you_go";
	private String broadcastLocation = "us_west_california";
	private String closedCaptionType = "none";
	private String deliveryMethod = "push";
	private String encoder = "wowza_gocoder";
	private Boolean hostedPage = false;
	private Boolean hostedPageSharingIcons = true;
	private String name;
	private Boolean playerCountdown = true;
	private Boolean playerResponsive = true;
	private String playerType = "wowza";
	private Integer playerWidth = 0;
	private Boolean recording = false;
	private String targetDeliveryProtocol = "hls_hds";
	private String transcoderType = "transcoded";
	private Boolean useStreamSource = false;
	private String wowzaEventId = "";
	private Date startTranscoderDate = null;
	private Date stopTranscoderDate = null;

	
	
	
	
	/**
	 * @return the wowzaEventId
	 */
	public String getWowzaEventId() {
		return wowzaEventId;
	}
	/**
	 * @param wowzaEventId the wowzaEventId to set
	 */
	public void setWowzaEventId(String wowzaEventId) {
		this.wowzaEventId = wowzaEventId;
	}
	/**
	 * @return the startTranscoderDate
	 */
	public Date getStartTranscoderDate() {
		return startTranscoderDate;
	}
	/**
	 * @param startTranscoderDate the startTranscoderDate to set
	 */
	public void setStartTranscoderDate(Date startTranscoderDate) {
		this.startTranscoderDate = startTranscoderDate;
	}
	/**
	 * @return the stopTranscoderDate
	 */
	public Date getStopTranscoderDate() {
		return stopTranscoderDate;
	}
	/**
	 * @param stopTranscoderDate the stopTranscoderDate to set
	 */
	public void setStopTranscoderDate(Date stopTranscoderDate) {
		this.stopTranscoderDate = stopTranscoderDate;
	}
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	/**
	 * @return the connectionType
	 */
	public String getConnectionType() {
		return connectionType;
	}
	/**
	 * @param connectionType the connectionType to set
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	/**
	 * @return the aspectRatioHeight
	 */
	public Integer getAspectRatioHeight() {
		return aspectRatioHeight;
	}
	/**
	 * @param aspectRatioHeight the aspectRatioHeight to set
	 */
	public void setAspectRatioHeight(Integer aspectRatioHeight) {
		this.aspectRatioHeight = aspectRatioHeight;
	}
	/**
	 * @return the aspectRatioWidth
	 */
	public Integer getAspectRatioWidth() {
		return aspectRatioWidth;
	}
	/**
	 * @param aspectRatioWidth the aspectRatioWidth to set
	 */
	public void setAspectRatioWidth(Integer aspectRatioWidth) {
		this.aspectRatioWidth = aspectRatioWidth;
	}
	/**
	 * @return the billingMode
	 */
	public String getBillingMode() {
		return billingMode;
	}
	/**
	 * @param billingMode the billingMode to set
	 */
	public void setBillingMode(String billingMode) {
		this.billingMode = billingMode;
	}
	/**
	 * @return the broadcastLocation
	 */
	public String getBroadcastLocation() {
		return broadcastLocation;
	}
	/**
	 * @param broadcastLocation the broadcastLocation to set
	 */
	public void setBroadcastLocation(String broadcastLocation) {
		this.broadcastLocation = broadcastLocation;
	}
	/**
	 * @return the closedCaptionType
	 */
	public String getClosedCaptionType() {
		return closedCaptionType;
	}
	/**
	 * @param closedCaptionType the closedCaptionType to set
	 */
	public void setClosedCaptionType(String closedCaptionType) {
		this.closedCaptionType = closedCaptionType;
	}
	/**
	 * @return the deliveryMethod
	 */
	public String getDeliveryMethod() {
		return deliveryMethod;
	}
	/**
	 * @param deliveryMethod the deliveryMethod to set
	 */
	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	/**
	 * @return the encoder
	 */
	public String getEncoder() {
		return encoder;
	}
	/**
	 * @param encoder the encoder to set
	 */
	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}
	/**
	 * @return the hostedPage
	 */
	public Boolean getHostedPage() {
		return hostedPage;
	}
	/**
	 * @param hostedPage the hostedPage to set
	 */
	public void setHostedPage(Boolean hostedPage) {
		this.hostedPage = hostedPage;
	}
	/**
	 * @return the hostedPageSharingIcons
	 */
	public Boolean getHostedPageSharingIcons() {
		return hostedPageSharingIcons;
	}
	/**
	 * @param hostedPageSharingIcons the hostedPageSharingIcons to set
	 */
	public void setHostedPageSharingIcons(Boolean hostedPageSharingIcons) {
		this.hostedPageSharingIcons = hostedPageSharingIcons;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the playerCountdown
	 */
	public Boolean getPlayerCountdown() {
		return playerCountdown;
	}
	/**
	 * @param playerCountdown the playerCountdown to set
	 */
	public void setPlayerCountdown(Boolean playerCountdown) {
		this.playerCountdown = playerCountdown;
	}
	/**
	 * @return the playerResponsive
	 */
	public Boolean getPlayerResponsive() {
		return playerResponsive;
	}
	/**
	 * @param playerResponsive the playerResponsive to set
	 */
	public void setPlayerResponsive(Boolean playerResponsive) {
		this.playerResponsive = playerResponsive;
	}
	/**
	 * @return the playerType
	 */
	public String getPlayerType() {
		return playerType;
	}
	/**
	 * @param playerType the playerType to set
	 */
	public void setPlayerType(String playerType) {
		this.playerType = playerType;
	}
	/**
	 * @return the playerWidth
	 */
	public Integer getPlayerWidth() {
		return playerWidth;
	}
	/**
	 * @param playerWidth the playerWidth to set
	 */
	public void setPlayerWidth(Integer playerWidth) {
		this.playerWidth = playerWidth;
	}
	/**
	 * @return the recording
	 */
	public Boolean getRecording() {
		return recording;
	}
	/**
	 * @param recording the recording to set
	 */
	public void setRecording(Boolean recording) {
		this.recording = recording;
	}
	/**
	 * @return the targetDeliveryProtocol
	 */
	public String getTargetDeliveryProtocol() {
		return targetDeliveryProtocol;
	}
	/**
	 * @param targetDeliveryProtocol the targetDeliveryProtocol to set
	 */
	public void setTargetDeliveryProtocol(String targetDeliveryProtocol) {
		this.targetDeliveryProtocol = targetDeliveryProtocol;
	}
	/**
	 * @return the transcoderType
	 */
	public String getTranscoderType() {
		return transcoderType;
	}
	/**
	 * @param transcoderType the transcoderType to set
	 */
	public void setTranscoderType(String transcoderType) {
		this.transcoderType = transcoderType;
	}
	/**
	 * @return the useStreamSource
	 */
	public Boolean getUseStreamSource() {
		return useStreamSource;
	}
	/**
	 * @param useStreamSource the useStreamSource to set
	 */
	public void setUseStreamSource(Boolean useStreamSource) {
		this.useStreamSource = useStreamSource;
	}
	
	
	public JSONObject getJSONObject() {
		JSONObject jObj = new JSONObject();
		jObj.put("aspect_ratio_height", aspectRatioHeight);
		jObj.put("aspect_ratio_width", aspectRatioWidth);
		jObj.put("billing_mode", billingMode);
		jObj.put("broadcast_location", broadcastLocation);
		jObj.put("closed_caption_type", closedCaptionType);
		jObj.put("delivery_method", deliveryMethod);
		jObj.put("encoder", encoder);
		jObj.put("hosted_page", hostedPage);
		jObj.put("hosted_page_sharing_icons", hostedPageSharingIcons);
		jObj.put("name", name);
		jObj.put("player_countdown", playerCountdown);
		jObj.put("player_responsive", playerResponsive);
		jObj.put("player_type", playerType);
		jObj.put("player_width", playerWidth);
		jObj.put("recording", recording);
		jObj.put("target_delivery_protocol", targetDeliveryProtocol);
		jObj.put("transcoder_type", transcoderType);
		jObj.put("use_stream_source", useStreamSource);
		
		return jObj;
	}

	public JSONObject getCloudJSONObjectForEngine() {
		JSONObject jObj = new JSONObject();
		jObj.put("broadcast_location", broadcastLocation);
		jObj.put("delivery_type", "single-bitrate");
		jObj.put("delivery_method", deliveryMethod);
		jObj.put("encoder", "wowza_streaming_engine");
		jObj.put("name", name);
		jObj.put("delivery_protocol", "hls");
		jObj.put("target_delivery_protocol", "hls");
		jObj.put("player_responsive", true);
		jObj.put("aspect_ratio_height", aspectRatioHeight);
		jObj.put("aspect_ratio_width", aspectRatioWidth);
		jObj.put("hosted_page", false);
		jObj.put("player_type", playerType);
		jObj.put("low_latency", true);
		
		JSONObject jObj1 = new JSONObject();
		jObj1.put("live_stream", jObj);
		
		return jObj1;
	}

	public JSONObject getCloudJSONObjectForSchedule() {
		JSONObject jObj = new JSONObject();
		jObj.put("action_type", "start_stop");
		jObj.put("name", name + "Scheduler");
		jObj.put("transcoder_id", wowzaEventId);
//		jObj.put("transcoder_name", "wowza_streaming_engine");
		jObj.put("recurrence_type", "once");
		jObj.put("start_transcoder", startTranscoderDate);
		jObj.put("stop_transcoder", stopTranscoderDate);
		
		JSONObject jObj1 = new JSONObject();
		jObj1.put("schedule", jObj);
		
		return jObj1;
	}

	public JSONObject getJSONObjectForServer(String connectionCode, String streamName) {
		JSONObject jObj = new JSONObject();
		jObj.put("enabled", true);
		jObj.put("entryName", streamName);
		jObj.put("port", 1935);
		jObj.put("profile", "wowza-streaming-cloud");
		jObj.put("sourceStreamName",streamName);
		jObj.put("connectionCode", connectionCode);
		jObj.put("autoStartTranscoder", true);
		jObj.put("wowzaCloud.adaptiveStreaming", "true");
		JSONObject sObj = new JSONObject();
		sObj.put("wowzaCloudDestinationType", "transcoder");
		sObj.put("destinationName", "wowzastreamingcloud");
		
		jObj.put("extraOptions", sObj);
		
		
		
		return jObj;
	}
}
