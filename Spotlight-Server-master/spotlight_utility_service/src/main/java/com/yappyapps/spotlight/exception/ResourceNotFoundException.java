package com.yappyapps.spotlight.exception;

/**
 * The ResourceNotFoundException class is the exception when requested resource
 * is not found.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class ResourceNotFoundException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2602488571025688101L;

	/**
	 * Constructor
	 * 
	 */
	public ResourceNotFoundException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : Throwable
	 * 
	 */
	public ResourceNotFoundException(Throwable cause) {
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
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
