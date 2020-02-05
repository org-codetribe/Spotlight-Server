package com.yappyapps.spotlight.exception;

/**
 * The AlreadyExistException class is the exception when the record to be
 * inserted is already present.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class AlreadyExistException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2302358509016275922L;

	/**
	 * Constructor
	 * 
	 */
	public AlreadyExistException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public AlreadyExistException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : Throwable
	 * 
	 */
	public AlreadyExistException(Throwable cause) {
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
	public AlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
