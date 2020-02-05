package com.yappyapps.spotlight.security;

import java.io.Serializable;

/**
 * The JwtAuthenticationRequest class is the bean for authentication request
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class JwtAuthenticationRequest implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1L;

	/**
	 * username
	 */
	private String username;

	/**
	 * password
	 */
	private String password;

	/**
	 * sourceIpAddress
	 */
	private String sourceIpAddress;

	/**
	 * forceLogin
	 */
	private Boolean forceLogin = false;

	/**
	 * Constructor
	 */
	public JwtAuthenticationRequest() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param username:
	 *            String
	 * @param password:
	 *            String
	 * 
	 */
	public JwtAuthenticationRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
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
	 * @return the forceLogin
	 */
	public Boolean getForceLogin() {
		return forceLogin;
	}

	/**
	 * @param forceLogin the forceLogin to set
	 */
	public void setForceLogin(Boolean forceLogin) {
		this.forceLogin = forceLogin;
	}
	
	

}
