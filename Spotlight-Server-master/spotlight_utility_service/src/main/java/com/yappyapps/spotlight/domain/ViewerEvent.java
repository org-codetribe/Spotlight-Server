package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The ViewerEvent class is the domain class which maps the ViewerEvent java
 * object to viewer_event table.
 * 
 * <h1>@Entity</h1> will enable it to map to database table.
 * 
 * <h1>@Table</h1> provides the database table name
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Entity
@Table(name = "VIEWER_EVENT")
public class ViewerEvent {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "VIEWER_ID")
	private Viewer viewer = null;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "EVENT_ID")
	private Event event = null;

	@Column(name = "PURCHASE_DATETIME", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp purchaseDatetime;

	@Column(name = "PURCHASE_METHOD", columnDefinition = "enum('Coupon','Payment')")
	@NotNull
	private String purchaseMethod;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

	@Column(name = "viewer_chat_auth_key")
	@NotNull
	private String viewerChatAuthKey;

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
	 * @return the viewer
	 */
	public Viewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 *            the viewer to set
	 */
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return the purchaseDatetime
	 */
	public Timestamp getPurchaseDatetime() {
		return purchaseDatetime;
	}

	/**
	 * @param purchaseDatetime
	 *            the purchaseDatetime to set
	 */
	public void setPurchaseDatetime(Timestamp purchaseDatetime) {
		this.purchaseDatetime = purchaseDatetime;
	}

	/**
	 * @return the purchaseMethod
	 */
	public String getPurchaseMethod() {
		return purchaseMethod;
	}

	/**
	 * @param purchaseMethod
	 *            the purchaseMethod to set
	 */
	public void setPurchaseMethod(String purchaseMethod) {
		this.purchaseMethod = purchaseMethod;
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
	 * @return the viewerChatAuthKey
	 */
	public String getViewerChatAuthKey() {
		return viewerChatAuthKey;
	}

	/**
	 * @param viewerChatAuthKey the viewerChatAuthKey to set
	 */
	public void setViewerChatAuthKey(String viewerChatAuthKey) {
		this.viewerChatAuthKey = viewerChatAuthKey;
	}

}
