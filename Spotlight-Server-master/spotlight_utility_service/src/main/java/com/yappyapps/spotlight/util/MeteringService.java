package com.yappyapps.spotlight.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.AuditLog;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.helper.AuditLogHelper;
import com.yappyapps.spotlight.repository.IAuditLogRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * The MeteringService class is used to meter all the operations
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class MeteringService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MeteringService.class);

	/**
	 * JwtTokenUtil
	 */
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * ISpotlightUserRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserRepository spotlightUserRepository;

	/**
	 * IAuditLogRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IAuditLogRepository auditLogRepository;

	/**
	 * AuditLogHelper dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private AuditLogHelper auditLogHelper;

	/**
	 * This method is used to record the time taken by each operation.
	 * 
	 * @param controller:
	 *            String
	 * @param operation:
	 *            String
	 * @param totalTime:
	 *            long
	 * @param reqSize:
	 *            int
	 * 
	 * 
	 */
	public void record(String controller, String operation, long totalTime, int reqSize) {

		LOGGER.info("Total time taken by operation : " + operation + " : of " + controller + "Controller is :::: "
				+ totalTime);
	}

	public void record(String requestBody, String token, String controller, String operation, long totalTime, int reqSize) {
		String username = null;
		String authToken = null;

		if(token != null  && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				if(username.startsWith("_S")) {
				username = username.substring(2);
				SpotlightUser spotlightUser = spotlightUserRepository.findByUsername(username);
					if(spotlightUser != null && ( spotlightUser.getUserType().equalsIgnoreCase("Admin") || spotlightUser.getUserType().equalsIgnoreCase("SuperAdmin"))) {
						AuditLog auditLog = new AuditLog();
						auditLog.setDescription(requestBody);
						auditLog.setOperation(operation);
						auditLog.setModule(controller);
						auditLog.setSpotlightUser(spotlightUser);
						
						auditLog = auditLogHelper.populateAuditLog(auditLog);
						
						auditLogRepository.save(auditLog);
						
					}
				} else if(username.startsWith("_V")) {
					username = username.substring(2);
				}
				
			} catch (IllegalArgumentException e) {
				LOGGER.error("an error occured during getting username from token", e);
			} catch (ExpiredJwtException e) {
				LOGGER.warn("the token is expired and not valid anymore", e);
			}
		} else {
			LOGGER.warn("couldn't find bearer string, will ignore the header");
		}
		LOGGER.info("Total time taken by operation : " + operation + " performed by " + username + ": of " + controller + "Controller is :::: "
				+ totalTime);
	}

}