package com.yappyapps.spotlight.service.impl;

import com.yappyapps.spotlight.domain.Role;
import com.yappyapps.spotlight.domain.helper.RoleHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IRoleRepository;
import com.yappyapps.spotlight.service.IRoleService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class RoleService implements IRoleService {
	/**
	* Logger for the class.
	*/	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

	/**
	* IAccessRoleRepository dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private IRoleRepository roleRepository;

	/**
	* AccessRoleHelper  dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private RoleHelper roleHelper;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	* This method is used to create the AccessRole.
	*  
	* @param role: AccessRole
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
	public String createRole(Role role) throws AlreadyExistException, BusinessException, Exception {
		String result = null;
		
		Role roleEntity = null;
		try {
			if(roleRepository.existsRoleByName(role.getName()))
				throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);

			roleEntity = roleHelper.populateRole(role);
			roleRepository.save(roleEntity);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLE, roleHelper.buildResponseObject(role));
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
	public String getAllRoles() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<Role> roleList = null;
		try {
			roleList = (List<Role>) roleRepository.findAll();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(roleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLES, roleHelper.buildResponseObject(roleList));

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
	public String getAllRoles(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<Role> roleList = new ArrayList<>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<Role> roles = roleRepository.findAll(pageableRequest);
	        totalCount = roles.getTotalElements();
	        List<Role> roleEntities = roles.getContent();
	        for (Role roleEntity : roleEntities) {
	        	Role roleDto = new Role();
	            BeanUtils.copyProperties(roleEntity, roleDto);
	            roleList.add(roleDto);
	        }
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(roleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLES, roleHelper.buildResponseObject(roleList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, roleList.size());

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
	public String getRoleById(Integer roleId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<Role> role = null;
		
		try {
			role = roleRepository.findById(roleId);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(!role.isPresent()) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}


		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLE, roleHelper.buildResponseObject(role.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

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
	@Override
	public String updateRole(Role role) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<Role> roleEntity = null;
		try {
			roleEntity = roleRepository.findById(role.getId());
			if(!roleEntity.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			
			roleHelper.populateRole(role, roleEntity.get());
			roleRepository.save(roleEntity.get());
		}  catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.ROLE, roleHelper.buildResponseObject(roleEntity.get()));
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
	public String deleteRole(Integer roleId) throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		try {
			if(!roleRepository.existsById(roleId))
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			roleRepository.deleteById(roleId);
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
