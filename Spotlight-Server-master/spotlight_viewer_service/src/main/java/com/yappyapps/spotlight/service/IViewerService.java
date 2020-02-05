package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.Favorite;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The IViewerService interface declares   
 * all the operations to act upon Spotlight Users 
 * 
 * @author  Naveen Goswami
 * @version 1.0
 * @since   2018-07-14 
 */
/**
 * @author ADMIN
 *
 */
/**
 * @author ADMIN
 *
 */
public interface IViewerService {
	/**
	 * This method is used to create the Viewer.
	 * 
	 * @param viewerReqObj:
	 *            Viewer
	 * @return String: Response
	 * 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String createViewer(Viewer viewerReqObj) throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all Viewer.
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getAllViewers() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Viewers with paging and orderBy.
	 * 
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getAllViewers(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get Viewer by viewerId.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getViewer(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;


	/**
	 * This method is used to get all Favorites Broadcasters with paging and orderBy.
	 * 
	 * @param viewerId:
	 *            Integer
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getFavoriteBroadcasters(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Favorites Broadcasters with paging and orderBy.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getFavoriteBroadcasters(Integer viewerId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Favorites Events.
	 * 
	 * @param viewerId:
	 *            Integer
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getFavoriteEvents(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Favorites Events with paging and orderBy.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getFavoriteEvents(Integer viewerId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to change the password of Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @return String: Response
	 * 
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String changeViewerPassword(Viewer viewer) throws BusinessException, Exception;

	/**
	 * This method is used to verify the old password of Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @param oldPassword:
	 *            String
	 * @return String: Response
	 * 
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public boolean verifyOldPassword(Viewer viewer, String oldPassword) throws BusinessException, Exception;

	/**
	 * This method is used to update the Viewer.
	 * 
	 * @param viewerReqObj:
	 *            Viewer
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String updateViewer(Viewer viewerReqObj)
			throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to delete the Viewer by id.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String deleteViewer(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to mark Broadcaster as Favorite.
	 * 
	 * @param favoriteReqObj:
	 *            Favorite.
	 * @param favoriteFlag:
	 *            Boolean.
	 * @return String: Response
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String manageFavoriteBroadcaster(Favorite favoriteReqObj, Boolean favoriteFlag) throws InvalidParameterException, AlreadyExistException, BusinessException, Exception;
	
	/**
	 * This method is used to mark Event as Favorite.
	 * 
	 * @param favoriteReqObj:
	 *            Favorite.
	 * @param favoriteFlag:
	 *            Boolean.
	 * @return String: Response
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String manageFavoriteEvent(Favorite favoriteReqObj, Boolean favoriteFlag) throws InvalidParameterException, AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all Purchased Events.
	 * 
	 * @param viewerId:
	 *            Integer
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getPurchasedEvents(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Purchased Events with paging and orderBy.
	 * 
	 * @param viewerId:
	 *            Integer
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String getPurchasedEvents(Integer viewerId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	public String updateViewerChatAccess(Viewer viewer, int eventId, String access, boolean accessFlag) throws ResourceNotFoundException, BusinessException, Exception;

}
