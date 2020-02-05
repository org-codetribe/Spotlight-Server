package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The IBroadcasterInfoService interface declares all the operations to act upon
 * Spotlight Users
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

public interface IBroadcasterInfoService {

	/**
	 * This method is used to create the BroadcasterInfo User.
	 * 
	 * @param broadcasterInfoReqObj:
	 *            BroadcasterInfo
	 * @return String: Response
	 * 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String createBroadcasterInfo(BroadcasterInfo broadcasterInfoReqObj)
			throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo User.
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
	public String getAllBroadcasterInfos() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users by Genre.
	 * 
	 * @param genreId:
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

	public String getAllBroadcasterInfos(Integer genreId)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users by Genre Name.
	 * 
	 * @param genreName:
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

	public String getAllBroadcasterInfosByGenreName(String genreName)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users with paging and orderBy.
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

	public String getAllBroadcasterInfos(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users by Genre with paging and
	 * orderBy.
	 * 
	 * @param genreId:
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

	public String getAllBroadcasterInfos(Integer genreId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users by Genre Name with paging and
	 * orderBy.
	 * 
	 * @param genreName:
	 *            String
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

	public String getAllBroadcasterInfosByGenreName(String genreName, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending BroadcasterInfo Users.
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

	public String getTrendingBroadcasters(Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending BroadcasterInfo Users by status.
	 * 
	 * @param status:
	 *            String
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

	public String getTrendingBroadcasters(String status, Integer viewerId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending BroadcasterInfo Users with paging and
	 * orderBy.
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

	public String getTrendingBroadcasters(Integer viewerId, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all trending BroadcasterInfo Users by status with
	 * paging and orderBy.
	 * 
	 * @param status:
	 *            String
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

	public String getTrendingBroadcasters(String status, Integer viewerId, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users by status.
	 * 
	 * @param status:
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

	public String getBroadcastersByStatus(String status)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all BroadcasterInfo Users by status with
	 * paging and orderBy.
	 * 
	 * @param status:
	 *            String
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

	public String getBroadcastersByStatus(String status, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get BroadcasterInfo User by broadcasterInfoId.
	 * 
	 * @param broadcasterInfoId:
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

	public String getBroadcasterInfo(Integer broadcasterInfoId)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get BroadcasterInfo User by spotlightUserId.
	 * 
	 * @param spotlightUserId:
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

	public String getBroadcasterInfoBySpotlightUserId(Integer spotlightUserId)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the BroadcasterInfo User.
	 * 
	 * @param broadcasterInfoReqObj:
	 *            BroadcasterInfo
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

	public String updateBroadcasterInfo(BroadcasterInfo broadcasterInfoReqObj)
			throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to delete the BroadcasterInfo by id.
	 * 
	 * @param broadcasterInfoId:
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

	public String deleteBroadcasterInfo(Integer broadcasterInfoId)
			throws ResourceNotFoundException, BusinessException, Exception;

	public String getAllCategories() throws ResourceNotFoundException, BusinessException, Exception;

}
