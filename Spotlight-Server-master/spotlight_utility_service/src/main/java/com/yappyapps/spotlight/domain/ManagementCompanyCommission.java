package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;

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
 * The ManagementCompanyCommission class is the domain class which maps the
 * ManagementCompanyCommission java object to management_company_commission
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
@Entity
@Table(name = "MANAGEMENT_COMPANY_COMMISSION")
public class ManagementCompanyCommission {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "spotlight_user_id", nullable = false)
	private SpotlightUser viewer = null;

	@ManyToOne
	@JoinColumn(name = "broadcaster_info_id", nullable = false)
	private BroadcasterInfo broadcasterInfo = null;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event = null;

	@Column(name = "PERCENTAGE", columnDefinition = "Float")
	@NotNull
	private Float percentage;

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "UPDATED_ON", columnDefinition = "TimeStamp DEFAULT CURRENT_TIMESTAMP")
	@NotNull
	private Timestamp updatedOn;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

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
	public SpotlightUser getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 *            the viewer to set
	 */
	public void setViewer(SpotlightUser viewer) {
		this.viewer = viewer;
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
	 * @return the percentage
	 */
	public Float getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage
	 *            the percentage to set
	 */
	public void setPercentage(Float percentage) {
		this.percentage = percentage;
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
	 * @return the updatedOn
	 */
	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn
	 *            the updatedOn to set
	 */
	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
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

}
