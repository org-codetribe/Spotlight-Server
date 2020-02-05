package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The ISpotlightUserService interface declares   
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
public interface ISpotlightUserService {
	/**
	 * This method is used to create the SpotlightUser.
	 * 
	 * @param spotlightUserReqObj:
	 *            SpotlightUser
	 * @return String: Response
	 * 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public String createSpotlightUser(SpotlightUser spotlightUserReqObj)
			throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all SpotlightUser.
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

	public String getAllSpotlightUsers() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all SPotlightUsers with paging and orderBy.
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

	public String getAllSpotlightUsers(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get SpotlightUser by spotlightUserId.
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

	public String getSpotlightUser(Integer spotlightUserId)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Spotlight User by userType.
	 * 
	 * @param userType:
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

	public String getSpotlightUsersByType(String userType)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Spotlight User by userType with paging and
	 * orderBy.
	 * 
	 * @param userType:
	 *            String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
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

	public String getSpotlightUsersByType(String userType, Integer limit, Integer offset, String direction,
			String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Spotlight User by role.
	 * 
	 * @param roleName:
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

	public String getSpotlightUsersByRole(String roleName)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to change the password of Spotlight User.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @return String: Response
	 * 
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */

	public String changeSpotlightUserPassword(SpotlightUser spotlightUser) throws BusinessException, Exception;

	/**
	 * This method is used to verify the old password of Spotlight User.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @param oldPassword:
	 *            String
	 * @return String: Response
	 * 
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	public boolean verifyOldPassword(SpotlightUser spotlightUser, String oldPassword)
			throws BusinessException, Exception;

	/**
	 * This method is used to update the SpotlightUser.
	 * 
	 * @param spotlightUserReqObj:
	 *            SpotlightUser
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

	public String updateSpotlightUser(SpotlightUser spotlightUserReqObj)
			throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to update the SpotlightUser roles.
	 * 
	 * @param spotlightUserReqObj:
	 *            SpotlightUser
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

	public String updateSpotlightUserRole(SpotlightUser spotlightUserReqObj)
			throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to delete the SpotlightUser by id.
	 * 
	 * @param spotlightUserId:
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

	public String deleteSpotlightUser(Integer spotlightUserId)
			throws ResourceNotFoundException, BusinessException, Exception;
}
