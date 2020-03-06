package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The Viewer class is the domain class which maps the Viewer java object to
 * viewer table.
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
@Table(name = "VIEWER")
public class Viewer {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "USERNAME", length = 255, unique = true)
	@NotNull
	private String username;

	@Column(name = "FNAME", length = 255)
	private String fname;

	@Column(name = "LNAME", length = 255)
	private String lname;

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

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "UPDATED_ON", columnDefinition = "TimeStamp DEFAULT CURRENT_TIMESTAMP")
	private Timestamp updatedOn;

	@Column(name = "UNIQUE_NAME", length = 100, unique = true)
	@NotNull
	private String uniqueName;

	@Column(name = "CHAT_NAME", length = 255, unique = true)
	@NotNull
	private String chatName;

	@Column(name = "PROFILE_PICTURE",length = 1000)
	private String profilePicture;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

    @Transient
	private String socialLoginType;
	@Transient
	private String socialLoginToken;


	@Column(name = "facebook_Gmail_Ids", columnDefinition = "Text")
	private String facebookAndGmail;

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
	 * @return the fname
	 */
	public String getFname() {
		return fname;
	}

	/**
	 * @param fname
	 *            the fname to set
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}

	/**
	 * @return the lname
	 */
	public String getLname() {
		return lname;
	}

	/**
	 * @param lname
	 *            the lname to set
	 */
	public void setLname(String lname) {
		this.lname = lname;
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
	 * @return the chatName
	 */
	public String getChatName() {
		return chatName;
	}

	/**
	 * @param chatName the chatName to set
	 */
	public void setChatName(String chatName) {
		this.chatName = chatName;
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

	public String getSocialLoginType() {
		return socialLoginType;
	}

	public void setSocialLoginType(String socialLoginType) {
		this.socialLoginType = socialLoginType;
	}

	public String getSocialLoginToken() {
		return socialLoginToken;
	}

	public void setSocialLoginToken(String socialLoginToken) {
		this.socialLoginToken = socialLoginToken;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getFacebookAndGmail() {
		return facebookAndGmail;
	}

	public void setFacebookAndGmail(String facebookAndGmail) {
		this.facebookAndGmail = facebookAndGmail;
	}
}
