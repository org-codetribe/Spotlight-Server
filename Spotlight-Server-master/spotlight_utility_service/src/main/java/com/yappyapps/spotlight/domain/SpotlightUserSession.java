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
 * The SpotlightUserSession class is the domain class which maps the
 * SpotlightUserSession java object to spotlight_user_session table.
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
@Table(name = "SPOTLIGHT_USER_SESSION")
public class SpotlightUserSession {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "SOURCE_IP_ADDRESS", length = 50)
	@NotNull
	private String sourceIpAddress;

	@ManyToOne
	@JoinColumn(name = "spotlight_user_id", nullable = false)
	private SpotlightUser spotlightUser = null;

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

	@Column(name = "AUTH_TOKEN", nullable = false)
	@NotNull
	private String authToken;

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
	 * @return the sourceIpAddress
	 */
	public String getSourceIpAddress() {
		return sourceIpAddress;
	}

	/**
	 * @param sourceIpAddress
	 *            the sourceIpAddress to set
	 */
	public void setSourceIpAddress(String sourceIpAddress) {
		this.sourceIpAddress = sourceIpAddress;
	}

	/**
	 * @return the spotlightUser
	 */
	public SpotlightUser getSpotlightUser() {
		return spotlightUser;
	}

	/**
	 * @param spotlightUser
	 *            the spotlightUser to set
	 */
	public void setSpotlightUser(SpotlightUser spotlightUser) {
		this.spotlightUser = spotlightUser;
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
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken
	 *            the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
