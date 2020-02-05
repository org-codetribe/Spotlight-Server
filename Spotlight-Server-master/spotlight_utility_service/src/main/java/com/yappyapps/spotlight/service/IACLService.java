package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AccessDeniedException;
import com.yappyapps.spotlight.exception.BusinessException;

/**
 * The IACLService interface declares all the operations to verify access by the user
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-10-01
 */
public interface IACLService {

	/**
	* This method is used to verify the access.
	*  
	* @param token: String
	* @param event: Event
	* @return Boolean: Response
	* 
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	*/	
	public Boolean isAllowed(String token, Event event) throws AccessDeniedException, BusinessException, Exception;
	
	
	public SpotlightUser getSpotlightUser(String token) throws AccessDeniedException, BusinessException, Exception;
	
	
	public BroadcasterInfo getBroadcasterInfo(String token) throws AccessDeniedException, BusinessException, Exception;
	
	
	public Viewer getViewer(String token) throws AccessDeniedException, BusinessException, Exception;

}
