package com.yappyapps.spotlight.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AccountDisabledException;
import com.yappyapps.spotlight.exception.SpotlightAuthenticationException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.security.JwtAuthenticationRequest;

/**
* The IViewerService interface declares   
* all the operations to act upon Viewers 
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
public interface IViewerService extends UserDetailsService {

	/**
	 * This method is used to create the authentication token.
	 * 
	 * @param authenticationRequest:
	 *            JwtAuthenticationRequest
	 * @return String: Response
	 * 
	 * @throws SpotlightAuthenticationException
	 *             SpotlightAuthenticationException
	 * @throws AccountDisabledException
	 *             AccountDisabledException
	 * @throws BusinessException
	 *             BusinessException
	 */
	public String createAuthenticationToken(JwtAuthenticationRequest authenticationRequest) throws SpotlightAuthenticationException, AccountDisabledException, BusinessException;

	/**
	 * This method is used to get the details of authenticated user.
	 * 
	 * @param username:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws AccountDisabledException
	 *             AccountDisabledException
	 * @throws BusinessException
	 *             BusinessException
	 */
	public String getAuthenticatedUser(String username)  throws ResourceNotFoundException, AccountDisabledException, BusinessException;

	/**
	 * This method is used to reset the password of Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String resetViewerPassword(Viewer viewer)  throws ResourceNotFoundException, BusinessException, Exception;

	
	public String createViewer(Viewer viewer)  throws BusinessException, Exception;
	
	/**
	 * This method is used to logout the Viewer.
	 * 
	 * @param token:
	 *            String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String logoutViewer(String token)  throws ResourceNotFoundException, BusinessException, Exception;

	public String findByEmail(String email)  throws ResourceNotFoundException, BusinessException, Exception;
	
}
