package com.yappyapps.spotlight.security;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * The JwtAuthenticationEntryPoint class is the main entry point when user is
 * not authenticated or unauthorized
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is called by Spring security in case of unauthenticated or
	 * unauthorized user.
	 * 
	 * @param request:
	 *            HttpServletRequest
	 * @param response:
	 *            HttpServletResponse
	 * @param authException:
	 *            AuthenticationException
	 * 
	 * @throws IOException
	 *             IOException
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		LOGGER.error(
				"User is not authenticated. throwing AuthenticationException     :::::::::::::::::::::::::::::       "
						+ authException.getMessage());
		authException.getMessage();
		// This is invoked when user tries to access a secured REST resource without
		// supplying any credentials
		// We should just send a 401 Unauthorized response because there is no 'login
		// page' to redirect to
		
        String message;
        if(authException.getCause() != null) {
            message = authException.getCause().getMessage();
        } else {
            message = authException.getMessage();
        }

		JSONObject errorObj = new JSONObject();
		errorObj.put("timestamp", System.currentTimeMillis());
		errorObj.put("status", HttpServletResponse.SC_UNAUTHORIZED);
		errorObj.put("error", "UnAuthorized");
		errorObj.put("exception", authException.getClass());
		errorObj.put("message", message);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(errorObj.toString());

	}
}