package com.yappyapps.spotlight.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yappyapps.spotlight.domain.JwtAuthToken;

/**
 * The JwtAuthorizationTokenFilter class is the implementation of
 * OncePerRequestFilter which will authenticate and authorize all he requests.
 * 
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	/**
	 * Logger for the class.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * tokenHeader
	 */
	@Value("${jwt.header}")
	private String tokenHeader;

	/**
	 * This method is called every time when a request is made.
	 * 
	 * @param request:
	 *            HttpServletRequest.
	 * @param response:
	 *            HttpServletResponse
	 * @param chain:
	 *            FilterChain
	 * 
	 * @throws ServletException
	 *             ServletException
	 * @throws IOException
	 *             IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		logger.debug("processing authentication for '{}'", request.getRequestURL());

		final String requestHeader = request.getHeader(this.tokenHeader);

		final String pathInfo = request.getPathInfo();

		final String requestUri = request.getRequestURI();

		logger.info("pathInfo :::: " + pathInfo);

		logger.info("requestUri :::: " + requestUri);

		String authToken = null;
		if (!(requestUri.startsWith("/api/1.0/login/viewer") || requestUri.startsWith("/api/1.0/login/user")
				|| requestUri.startsWith("/api/1.0/logout/viewer") || requestUri.startsWith("/api/1.0/logout/user")
				|| requestUri.startsWith("/api/1.0/search/") || requestUri.startsWith("/api/1.0/register/viewer")
				|| requestUri.startsWith("/api/1.0/resetViewerPassword") || requestUri.startsWith("/api/1.0/resetUserPassword")
				|| requestUri.startsWith("/api/1.0/event/public/") || requestUri.startsWith("/api/1.0/broadcaster/public/"))
				&& requestHeader != null && requestHeader.startsWith("Bearer ")) {
			authToken = requestHeader.substring(7);
			JwtAuthToken token = new JwtAuthToken(authToken);
			SecurityContextHolder.getContext().setAuthentication(token);

		} else {
			logger.warn("couldn't find bearer string, will ignore the header");
		}

		chain.doFilter(request, response);
	}
}