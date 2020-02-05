package com.yappyapps.spotlight.exception;

/**
 * The BusinessException class is the base exception.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class BusinessException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2395010760061987264L;

	/**
	 * Constructor
	 * 
	 */
	public BusinessException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public BusinessException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : Throwable
	 * 
	 */
	public BusinessException(Throwable cause) {
		super(cause);
	}

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
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

}
