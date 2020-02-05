package com.yappyapps.spotlight.exception;

/**
 * The InvalidParameterException class is the exception when mandatory request
 * parameters are either empty or invalid.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class InvalidParameterException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2057420519735113468L;

	/**
	 * Constructor
	 * 
	 */
	public InvalidParameterException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public InvalidParameterException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : Throwable
	 * 
	 */
	public InvalidParameterException(Throwable cause) {
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
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}
