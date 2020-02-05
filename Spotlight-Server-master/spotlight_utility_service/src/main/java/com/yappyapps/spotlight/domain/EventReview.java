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
 * The EventReview class is the domain class which maps the
 * EventReview java object to event_review table.
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
@Table(name = "EVENT_REVIEW")
public class EventReview {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "viewer_id", nullable = false)
	private Viewer viewer = null;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event = null;

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "IS_LIKE", length = 1)
	@NotNull
	private Boolean isLike;

	@Column(name = "COMMENT", columnDefinition = "Text")
	private String comment;

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
	 * @return the isLike
	 */
	public Boolean getIsLike() {
		return isLike;
	}

	/**
	 * @param isLike the isLike to set
	 */
	public void setIsLike(Boolean isLike) {
		this.isLike = isLike;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	
}
