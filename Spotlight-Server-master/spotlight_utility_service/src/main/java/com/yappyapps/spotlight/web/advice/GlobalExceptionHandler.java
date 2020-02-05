package com.yappyapps.spotlight.web.advice;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.yappyapps.spotlight.exception.AccessDeniedException;
import com.yappyapps.spotlight.exception.AccountDisabledException;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * This method is used to catch ResourceNotFoundException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	void handleNotFoundExceptions(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());
	}

	/**
	 * This method is used to catch ResourceNotFoundException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(AccessDeniedException.class)
	void handleAccessDeniedExceptions(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.FORBIDDEN.value());
	}

	/**
	 * This method is used to catch InvalidParameterException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(InvalidParameterException.class)
	void handleBadRequests(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * This method is used to catch AlreadyExistException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(AlreadyExistException.class)
	void handleAlreadyExistRequests(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.CONFLICT.value());
	}

	/**
	 * This method is used to catch SpotlightAuthenticationException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(SpotlightAuthenticationException.class)
	void handleSpotlightAuthenticationException(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.UNAUTHORIZED.value());
	}

	/**
	 * This method is used to catch AccountDisabledException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(AccountDisabledException.class)
	void handleAccountDisabledException(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.LOCKED.value());
	}

	/**
	 * This method is used to catch UsernameNotFoundException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	void handleUserNameNotFoundException(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * This method is used to catch BusinessException at Controller level.
	 * 
	 * @param response:
	 *            HttpServletResponse.
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@ExceptionHandler(BusinessException.class)
	void handleInternalServerErrors(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}


}
