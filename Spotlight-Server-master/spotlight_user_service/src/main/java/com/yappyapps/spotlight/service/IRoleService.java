package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.domain.Role;
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

public interface IRoleService {
	/**
	* This method is used to create the AccessRole.
	*  
	* @param role: Role
	* @return String: Response
	* 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	*/	
	public String createRole(Role role) throws AlreadyExistException, BusinessException, Exception;

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
	public String getAllRoles() throws ResourceNotFoundException, BusinessException, Exception;

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
	public String getAllRoles(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

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
	public String getRoleById(Integer roleId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the AccessRole.
	 * 
	 * @param role:
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
	public String updateRole(Role role) throws ResourceNotFoundException, BusinessException, Exception;

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
	public String deleteRole(Integer roleId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;


}

