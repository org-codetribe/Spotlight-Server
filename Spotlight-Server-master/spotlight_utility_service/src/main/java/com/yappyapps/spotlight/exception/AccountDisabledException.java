package com.yappyapps.spotlight.exception;

/**
 * The AccountDisabledException class is the exception when disabled account is
 * being trying to access.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class AccountDisabledException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4804255581828466340L;

	/**
	 * Constructor
	 * 
	 */
	public AccountDisabledException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : String
	 * 
	 */
	public AccountDisabledException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : Throwable
	 * 
	 */
	public AccountDisabledException(Throwable cause) {
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
	public AccountDisabledException(String message, Throwable cause) {
		super(message, cause);
	}

}
