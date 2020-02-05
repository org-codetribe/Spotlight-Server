package com.yappyapps.spotlight.util;

import java.math.BigDecimal;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.Viewer;

public class Payment {
	private String nonce;
	private Viewer viewer;
	private BroadcasterInfo broadcasterInfo;
	private Event event;
	private BigDecimal chargeAmount;
	private String couponCode;
	private Coupon coupon;
	/**
	 * @param nonce
	 * @param viewer
	 * @param event
	 * @param chargeAmount
	 * @param couponCode
	 */
	public Payment(String nonce, Viewer viewer, Event event, BigDecimal chargeAmount, String couponCode) {
		super();
		this.nonce = nonce;
		this.viewer = viewer;
		this.event = event;
		this.chargeAmount = chargeAmount;
		this.couponCode = couponCode;
	}

	public Payment(String nonce, BroadcasterInfo broadcasterInfo, Event event, BigDecimal chargeAmount, Coupon coupon) {
		super();
		this.nonce = nonce;
		this.broadcasterInfo = broadcasterInfo;
		this.event = event;
		this.chargeAmount = chargeAmount;
		this.coupon = coupon;
	}

//	public Payment(String nonce, Viewer viewer, BroadcasterInfo broadcasterInfo, Event event, BigDecimal chargeAmount, String couponCode) {
//		super();
//		this.nonce = nonce;
//		this.viewer = viewer;
//		this.broadcasterInfo = broadcasterInfo;
//		this.event = event;
//		this.chargeAmount = chargeAmount;
//		this.couponCode = couponCode;
//	}

	/**
	 * @return the nonce
	 */
	public String getNonce() {
		return nonce;
	}
	/**
	 * @param nonce the nonce to set
	 */
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	/**
	 * @return the viewer
	 */
	public Viewer getViewer() {
		return viewer;
	}
	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * @return the broadcasterInfo
	 */
	public BroadcasterInfo getBroadcasterInfo() {
		return broadcasterInfo;
	}

	/**
	 * @param broadcasterInfo the broadcasterInfo to set
	 */
	public void setBroadcasterInfo(BroadcasterInfo broadcasterInfo) {
		this.broadcasterInfo = broadcasterInfo;
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}
	/**
	 * @param event the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}
	/**
	 * @return the chargeAmount
	 */
	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}
	/**
	 * @param chargeAmount the chargeAmount to set
	 */
	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	/**
	 * @return the couponCode
	 */
	public String getCouponCode() {
		return couponCode;
	}
	/**
	 * @param couponCode the couponCode to set
	 */
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * @return the coupon
	 */
	public Coupon getCoupon() {
		return coupon;
	}

	/**
	 * @param coupon the coupon to set
	 */
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	
	
}
