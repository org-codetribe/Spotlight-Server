package com.yappyapps.spotlight.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * The SpotlightAuthenticationException class is the exception when authentication is
 * failed.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class SpotlightAuthenticationException extends AuthenticationException {

	/**
	 * serialVersionUID
	 */
//	private static final long serialVersionUID = 3640094764285390728L;

//	/**
//	 * Constructor
//	 * 
//	 */
//	public SpotlightAuthenticationException() {
//	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public SpotlightAuthenticationException(String message) {
		super(message);
	}

//	/**
//	 * Constructor
//	 * 
//	 * @param cause
//	 *            : Throwable
//	 * 
//	 */
//	public SpotlightAuthenticationException(Throwable cause) {
//		super(cause);
//	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * @param cause
	 *            : Throwable
	 * 
	 * 
	 */
	public SpotlightAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

}
