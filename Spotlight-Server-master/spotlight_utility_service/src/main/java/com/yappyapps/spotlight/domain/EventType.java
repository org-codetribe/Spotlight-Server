package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

/**
 * The EventType class is the domain class which maps the EventType java object
 * to event_type table.
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
@Table(name = "EVENT_TYPE")
public class EventType {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Field(termVector = TermVector.YES)
	@Column(name = "NAME", length = 255, unique=true)
	@NotNull
	private String name;

	@ManyToOne
	@JoinColumn(name = "event_type_pid")
	private EventType eventType;

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;
	@Column(name = "EVENT_TYPE_BANNER_URL", columnDefinition = "Text")
	@NotNull
	private String eventTypeBannerUrl;


	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

	@Column(name = "IS_CATEGORY", length = 1)
	@NotNull
	private Boolean isCategory = false;

	@OneToMany(mappedBy = "eventType", orphanRemoval = true)
	private List<EventType> children;

	/**
	 * @return the children
	 */
	public List<EventType> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<EventType> children) {
		this.children = children;
	}

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
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
	 * @return the isCategory
	 */
	public Boolean getIsCategory() {
		return isCategory;
	}

	/**
	 * @param isCategory the isCategory to set
	 */
	public void setIsCategory(Boolean isCategory) {
		this.isCategory = isCategory;
	}

	public String getEventTypeBannerUrl() {
		return eventTypeBannerUrl;
	}

	public void setEventTypeBannerUrl(String eventTypeBannerUrl) {
		this.eventTypeBannerUrl = eventTypeBannerUrl;
	}

	public Boolean getCategory() {
		return isCategory;
	}

	public void setCategory(Boolean category) {
		isCategory = category;
	}
}
