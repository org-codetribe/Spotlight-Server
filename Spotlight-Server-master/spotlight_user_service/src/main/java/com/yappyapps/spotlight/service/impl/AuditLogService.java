package com.yappyapps.spotlight.service.impl;

import java.util.ArrayList;
import java.util.List;

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

import com.yappyapps.spotlight.domain.AuditLog;
import com.yappyapps.spotlight.domain.helper.AuditLogHelper;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IAuditLogRepository;
import com.yappyapps.spotlight.service.IAuditLogService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;
/**
* The AuditLogService class is the implementation of IAuditLogService
* 
* <h1>@Service</h1> denotes that it is a service class
* * 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
@Service
public class AuditLogService implements IAuditLogService {
	/**
	* Logger for the class.
	*/	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);

	/**
	* IAuditLogRepository dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private IAuditLogRepository auditLogRepository;

	/**
	* AuditLogHelper auditLogHelper dependency will be automatically injected.
	* <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.  
	*/	
	@Autowired
	private AuditLogHelper auditLogHelper;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

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
	@Override
	public String getAllAuditLogs() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<AuditLog> auditLogList = null;
		try {
			auditLogList = (List<AuditLog>) auditLogRepository.findAll();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(auditLogList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.AUDITLOGS, auditLogHelper.buildResponseObject(auditLogList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}
	
	/**
	 * This method is used to get all AuditLogs with paging and orderBy.
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
	public String getAllAuditLogs(Integer limit, Integer offset, String direction, String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<AuditLog> auditLogList = new ArrayList<>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<AuditLog> auditLogs = auditLogRepository.findAll(pageableRequest);
	        totalCount = auditLogs.getTotalElements();
	        List<AuditLog> auditLogEntities = auditLogs.getContent();
	        for (AuditLog auditLogEntity : auditLogEntities) {
	        	AuditLog auditLogDto = new AuditLog();
	            BeanUtils.copyProperties(auditLogEntity, auditLogDto);
	            auditLogList.add(auditLogDto);
	        }
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		
		if(auditLogList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.AUDITLOGS, auditLogHelper.buildResponseObject(auditLogList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, auditLogList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}
	
}
