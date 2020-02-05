package com.yappyapps.spotlight.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.domain.helper.AccessRoleHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IAccessRoleRepository;
import com.yappyapps.spotlight.service.IAccessRoleService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;
/**
* The AccessRoleService class is the implementation of IAccessRoleService
* 
* <h1>@Service</h1> denotes that it is a service class
* * 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
@Service
public class AccessRoleService implements IAccessRoleService {
	/**
	* Logger for the class.
	*/	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessRoleService.class);

	/**
	* IAccessRoleRepository dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private IAccessRoleRepository accessRoleRepository;

	/**
	* AccessRoleHelper accessRoleHelper dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private AccessRoleHelper accessRoleHelper;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

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
	@Override
	public String createAccessRole(AccessRole accessRoleReqObj) throws AlreadyExistException, BusinessException, Exception {
		String result = null;
		
		AccessRole accessRoleEntity = null;
		try {
			if(accessRoleRepository.existsRoleByName(accessRoleReqObj.getName()))
				throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
			
			accessRoleEntity = accessRoleHelper.populateAccessRole(accessRoleReqObj);
			accessRoleRepository.save(accessRoleEntity);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLE, accessRoleHelper.buildResponseObject(accessRoleEntity));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getAllAccessRoles() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<AccessRole> accessRoleList = null;
		try {
			accessRoleList = (List<AccessRole>) accessRoleRepository.findAll();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(accessRoleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLES, accessRoleHelper.buildResponseObject(accessRoleList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}
	
	/**
	 * This method is used to get all AccessRoles with paging and orderBy.
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
	@Override
	public String getAllAccessRoles(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<AccessRole> accessRoleList = new ArrayList<>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<AccessRole> accessRoles = accessRoleRepository.findAll(pageableRequest);
	        totalCount = accessRoles.getTotalElements();
	        List<AccessRole> accessRoleEntities = accessRoles.getContent();
	        for (AccessRole accessRoleEntity : accessRoleEntities) {
	        	AccessRole accessRoleDto = new AccessRole();
	            BeanUtils.copyProperties(accessRoleEntity, accessRoleDto);
	            accessRoleList.add(accessRoleDto);
	        }
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(accessRoleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLES, accessRoleHelper.buildResponseObject(accessRoleList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, accessRoleList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}
	
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
	@Override
	public String getAccessRoleById(Integer roleId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<AccessRole> accessRole = null;
		
		try {
			accessRole = accessRoleRepository.findById(roleId);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(!accessRole.isPresent()) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}


		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLE, accessRoleHelper.buildResponseObject(accessRole.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

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
	@Override
	public String updateAccessRole(AccessRole accessRoleReqObj) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<AccessRole> accessRoleEntity = null;
		try {
			accessRoleEntity = accessRoleRepository.findById(accessRoleReqObj.getId());
			if(!accessRoleEntity.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			
			accessRoleHelper.populateAccessRole(accessRoleReqObj, accessRoleEntity.get());
			accessRoleRepository.save(accessRoleEntity.get());
		}  catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLE, accessRoleHelper.buildResponseObject(accessRoleEntity.get()));
		result = utils.constructSucessJSON(jObj);

		return result;
	}

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
	@Override
	public String deleteAccessRole(Integer roleId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		try {
			if(!accessRoleRepository.existsById(roleId))
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			accessRoleRepository.deleteById(roleId);
		}  catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new InvalidParameterException("Role could not be deleted as it is referenced by some other entity.");
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		
		result = utils.constructSucessJSON(jObj);
		return result;
	}

}
