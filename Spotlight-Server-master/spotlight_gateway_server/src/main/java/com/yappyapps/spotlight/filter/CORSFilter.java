package com.yappyapps.spotlight.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CORSFilter class is the filter which will allow cross origin requests.
 * 
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class CORSFilter  {

	//implements Filter
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(CORSFilter.class);

	/**
	 * Valid Methods allowed for CORS.
	 */
	public static String VALID_METHODS = "DELETE, HEAD, GET, OPTIONS, POST, PUT";

	/**
	 * This method is called when filter is being destroyed.
	 * 
	 */
	/*public void destroy() {
		LOGGER.debug("CORSFilter : destroy");
	}

	*//**
	 * This method is called every time when a request is made.
	 *
	 * @param req:
	 *            ServletRequest.
	 * @param resp:
	 *            ServletResponse
	 * @param chain:
	 *            FilterChain
	 *
	 * @throws ServletException
	 *             ServletException
	 * @throws IOException
	 *             IOException
	 *//*
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpResp = (HttpServletResponse) resp;
		LOGGER.debug("CORSFilter : doFilter");
		// No Origin header present means this is not a cross-domain request
		String origin = httpReq.getHeader("Origin");
		if (origin == null) {
			// Return standard response if OPTIONS request w/o Origin header
			if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
				httpResp.setHeader("Allow", VALID_METHODS);
				httpResp.setStatus(200);
				return;
			}
		} else {
			// This is a cross-domain request, add headers allowing access
			httpResp.setHeader("Access-Control-Allow-Origin", origin);
			httpResp.setHeader("Access-Control-Allow-Methods", VALID_METHODS);

			String headers = httpReq.getHeader("Access-Control-Request-Headers");
			if (headers != null)
				httpResp.setHeader("Access-Control-Allow-Headers", headers);

			// Allow caching cross-domain permission
			httpResp.setHeader("Access-Control-Max-Age", "3600");
		}
		// Pass request down the chain, except for OPTIONS
		if (!"OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
			chain.doFilter(req, resp);
		}
	}

	*//**
	 * This method is called at the time of initialization.
	 *
	 * @param config:
	 *            FilterConfig
	 *
	 * @throws ServletException
	 *             ServletException
	 *//*
	public void init(FilterConfig config) throws ServletException {
		LOGGER.debug("CORSFilter : init");
	}*/
}