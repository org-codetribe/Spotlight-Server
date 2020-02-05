package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
/**
* The IAccessRoleService interface declares  
* all the operations to act upon Access Roles 
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/

public interface IAccessRoleService  {
	/**
	* This method is used to create the AccessRole.
	*  
	* @param accessRoleReqObj: AccessRole
	* @return String: Response
	* 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	*/	
	public String createAccessRole(AccessRole accessRoleReqObj) throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all AccessRoles.
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
	public String getAllAccessRoles() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all AccessRoles with paging.
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
	public String getAllAccessRoles(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get AccessRole by roleId.
	 * 
	 * @param roleId:
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
	public String getAccessRoleById(Integer roleId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the AccessRole.
	 * 
	 * @param accessRoleReqObj:
	 *            AccessRole
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
	public String updateAccessRole(AccessRole accessRoleReqObj) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to delete the AccessRole by id.
	 * 
	 * @param roleId:
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
	public String deleteAccessRole(Integer roleId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;


}

