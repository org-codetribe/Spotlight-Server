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
 * The CommissionTransaction class is the domain class which maps the
 * CommissionTransaction java object to commission_transaction table.
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
@Table(name = "COMMISSION_TRANSACTION")
public class CommissionTransaction {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "PAYMENT_METHOD", length = 255)
	@NotNull
	private String paymentMethod;

	@Column(name = "AMOUNT", columnDefinition = "Float")
	@NotNull
	private Float amount;

	@ManyToOne
	@JoinColumn(name = "spotlight_user_id", nullable = false)
	private SpotlightUser spotlightUser = null;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event = null;

	@Column(name = "payment_datetime", columnDefinition = "TimeStamp DEFAULT CURRENT_TIMESTAMP")
	@NotNull
	private Timestamp paymentDatetime;

	@Column(name = "TRANSACTION_ID")
	@NotNull
	private String transactionId;

	@Column(name = "payment_type", columnDefinition = "enum('Debit','Credit')")
	@NotNull
	private String paymentType;

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
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param paymentMethod
	 *            the paymentMethod to set
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * @return the amount
	 */
	public Float getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Float amount) {
		this.amount = amount;
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
	 * @return the paymentDatetime
	 */
	public Timestamp getPaymentDatetime() {
		return paymentDatetime;
	}

	/**
	 * @param paymentDatetime
	 *            the paymentDatetime to set
	 */
	public void setPaymentDatetime(Timestamp paymentDatetime) {
		this.paymentDatetime = paymentDatetime;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 *            the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the paymentType
	 */
	public String getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType
	 *            the paymentType to set
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
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
