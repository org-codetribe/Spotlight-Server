package com.yappyapps.spotlight.security;

import java.io.Serializable;
import java.util.Date;

/**
 * The JwtAuthenticationResponse class is the bean for authentication response
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class JwtAuthenticationResponse implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * token
	 */
	private final String token;

	/**
	 * username
	 */
	private final String username;

	/**
	 * issuedAt
	 */
	private final Date issuedAt;

	/**
	 * expiresAt
	 */
	private final Date expiresAt;

	/**
	 * tokenType
	 */
	private final String tokenType;

	/**
	 * Constructor
	 * 
	 * @param token:
	 *            String
	 * @param username:
	 *            String
	 * @param issuedAt:
	 *            Date
	 * @param expiresAt:
	 *            Date
	 * 
	 */
	public JwtAuthenticationResponse(String token, String username, Date issuedAt, Date expiresAt) {
		this.token = token;
		this.username = username;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
		this.tokenType = "Bearer";
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the issuedAt
	 */
	public Date getIssuedAt() {
		return this.issuedAt;
	}

	/**
	 * @return the expiresAt
	 */
	public Date getExpiresAt() {
		return this.expiresAt;
	}

	/**
	 * @return the tokenType
	 */
	public String getTokenType() {
		return this.tokenType;
	}
}
