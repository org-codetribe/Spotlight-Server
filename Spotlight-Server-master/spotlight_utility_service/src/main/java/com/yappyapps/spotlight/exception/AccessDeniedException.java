package com.yappyapps.spotlight.exception;

/**
 * The AccessDeniedException class is the exception when requested resource
 * is allowed to access.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class AccessDeniedException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2602488571025688101L;

	/**
	 * Constructor
	 * 
	 */
	public AccessDeniedException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public AccessDeniedException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : Throwable
	 * 
	 */
	public AccessDeniedException(Throwable cause) {
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
	public AccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

}
