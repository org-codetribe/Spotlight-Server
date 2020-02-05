package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The PricingRule class is the domain class which maps the PricingRule java
 * object to pricing_rule table.
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
@Table(name = "PRICING_RULE")
public class PricingRule {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "EVENT_DURATION")
	@NotNull
	private Integer eventDuration;

	@Column(name = "MINIMUM_PRICE", columnDefinition = "Float")
	@NotNull
	private Float minimumPrice;

	@Column(name = "DISCOUNT_PERCENTAGE", columnDefinition = "Float")
	private Float discountPercentage;

	@Column(name = "DISCOUNT_VALID_TILL")
	private Timestamp discountValidTill;

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
	 * @return the eventDuration
	 */
	public Integer getEventDuration() {
		return eventDuration;
	}

	/**
	 * @param eventDuration
	 *            the eventDuration to set
	 */
	public void setEventDuration(Integer eventDuration) {
		this.eventDuration = eventDuration;
	}

	/**
	 * @return the minimumPrice
	 */
	public Float getMinimumPrice() {
		return minimumPrice;
	}

	/**
	 * @param minimumPrice
	 *            the minimumPrice to set
	 */
	public void setMinimumPrice(Float minimumPrice) {
		this.minimumPrice = minimumPrice;
	}

	/**
	 * @return the discountPercentage
	 */
	public Float getDiscountPercentage() {
		return discountPercentage;
	}

	/**
	 * @param discountPercentage
	 *            the discountPercentage to set
	 */
	public void setDiscountPercentage(Float discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	/**
	 * @return the discountValidTill
	 */
	public Timestamp getDiscountValidTill() {
		return discountValidTill;
	}

	/**
	 * @param discountValidTill
	 *            the discountValidTill to set
	 */
	public void setDiscountValidTill(Timestamp discountValidTill) {
		this.discountValidTill = discountValidTill;
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
