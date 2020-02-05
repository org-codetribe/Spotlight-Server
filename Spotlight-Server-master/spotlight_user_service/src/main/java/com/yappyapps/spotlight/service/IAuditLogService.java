package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
/**
* The IAccessRoleService interface declares  
* all the operations to act upon Access Roles 
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/

public interface IAuditLogService  {

	/**
	 * This method is used to get all AuditLogs.
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
	public String getAllAuditLogs() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all AuditLogs with paging.
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
	public String getAllAuditLogs(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception;

}

