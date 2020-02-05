package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

/**
 * The SpotlightUser class is the domain class which maps the SpotlightUser java
 * object to spotlight_user table.
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
@Table(name = "SPOTLIGHT_USER")
public class SpotlightUser {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Field(termVector = TermVector.YES)
	@Column(name = "NAME", length = 255)
	@NotNull
	private String name;

	@Column(name = "USERNAME", length = 255, unique = true)
	@NotNull
	private String username;

	@Field(termVector = TermVector.YES)
	@Column(name = "PHONE", length = 50)
	private String phone;

	@Column(name = "EMAIL", length = 255, unique = true)
	@NotNull
	@Size(min = 4, max = 255)
	private String email;

	@Column(name = "PASSWORD", length = 100)
	@NotNull
	@Size(min = 4, max = 100)
	private String password;

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

	@Column(name = "PAYPAL_EMAIL_ID", length = 255)
	private String paypalEmailId;

	@Column(name = "USER_TYPE", columnDefinition = "enum('SUPERADMIN','ADMIN','BROADCASTER','SALES','MANAGEMENTCOMPANY')")
	//@NotNull
	private String userType;

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "UPDATED_ON", columnDefinition = "TimeStamp  DEFAULT CURRENT_TIMESTAMP")
	private Timestamp updatedOn;

	@Column(name = "UNIQUE_NAME", length = 100, unique = true)
	@NotNull
	private String uniqueName;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "ACCESS_ROLE_USER", joinColumns = {
			@JoinColumn(name = "SPOTLIGHT_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ACCESS_ROLE_ID") })
	private Set<AccessRole> roles = new HashSet<AccessRole>(0);


	/*@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<Role> userRoles;*/

	transient String token;


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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1
	 *            the address1 to set
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
	 * @param address2
	 *            the address2 to set
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
	 * @param city
	 *            the city to set
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
	 * @param state
	 *            the state to set
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
	 * @param zip
	 *            the zip to set
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
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the paypalEmailId
	 */
	public String getPaypalEmailId() {
		return paypalEmailId;
	}

	/**
	 * @param paypalEmailId
	 *            the paypalEmailId to set
	 */
	public void setPaypalEmailId(String paypalEmailId) {
		this.paypalEmailId = paypalEmailId;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
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
	 * @return the roles
	 */
	public Set<AccessRole> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(Set<AccessRole> roles) {
		this.roles = roles;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
