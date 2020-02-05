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

import com.yappyapps.spotlight.domain.PricingRule;
import com.yappyapps.spotlight.domain.helper.PricingRuleHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IPricingRuleRepository;
import com.yappyapps.spotlight.service.IPricingRuleService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The PricingRuleService class is the implementation of IPricingRuleService
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Service
public class PricingRuleService implements IPricingRuleService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PricingRuleService.class);

	/**
	 * IPricingRuleRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IPricingRuleRepository pricingRuleRepository;

	/**
	 * IPricingRuleRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private PricingRuleHelper pricingRuleHelper;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * This method is used to create the PricingRule
	 * 
	 * @param pricingRuleReqObj:
	 *            PricingRule
	 * @return String: Response
	 * 
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 * 
	 */
	@Override
	public String createPricingRule(PricingRule pricingRuleReqObj)
			throws AlreadyExistException, BusinessException, Exception {
		String result = null;
		

		PricingRule pricingRuleEntity = new PricingRule();
		try {
			pricingRuleEntity = pricingRuleHelper.populatePricingRule(pricingRuleReqObj);
			pricingRuleEntity = pricingRuleRepository.save(pricingRuleEntity);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULE, pricingRuleHelper.buildResponseObject(pricingRuleEntity));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all PricingRules.
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
	public String getAllPricingRules() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<PricingRule> pricingRuleEntityList = null;
		try {
			pricingRuleEntityList = (List<PricingRule>) pricingRuleRepository.findAll();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (pricingRuleEntityList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULES, pricingRuleHelper.buildResponseObject(pricingRuleEntityList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all PricingRules with paging.
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
	public String getAllPricingRules(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<PricingRule> pricingRuleList = new ArrayList<PricingRule>();
		int pageNum = offset / limit;

		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<PricingRule> pricingRules = pricingRuleRepository.findAll(pageableRequest);
			totalCount = pricingRules.getTotalElements();
			List<PricingRule> pricingRuleEntities = pricingRules.getContent();
			for (PricingRule pricingRuleEntity : pricingRuleEntities) {
				PricingRule pricingRuleDto = new PricingRule();
				BeanUtils.copyProperties(pricingRuleEntity, pricingRuleDto);
				pricingRuleList.add(pricingRuleDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (pricingRuleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULES, pricingRuleHelper.buildResponseObject(pricingRuleList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, pricingRuleList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all PricingRule by id.
	 * 
	 * @param pricingRuleId:
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
	@Override
	public String getPricingRule(Integer pricingRuleId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<PricingRule> pricingRule = null;
		
		try {
			pricingRule = pricingRuleRepository.findById(pricingRuleId);
			if (!pricingRule.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULE, pricingRuleHelper.buildResponseObject(pricingRule.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to get all PricingRules by status.
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
	@Override
	public String getPricingRuleByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		List<PricingRule> pricingRuleList = null;
		
		try {
			pricingRuleList = pricingRuleRepository.findAllByStatus(status);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (pricingRuleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULES, pricingRuleHelper.buildResponseObject(pricingRuleList));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all PricingRules by status with paging.
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
	@Override
	public String getPricingRuleByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<PricingRule> pricingRuleList = new ArrayList<PricingRule>();
		int pageNum = offset / limit;

		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<PricingRule> pricingRules = pricingRuleRepository.findAllByStatus(status, pageableRequest);
			totalCount = pricingRules.getTotalElements();
			List<PricingRule> pricingRuleEntities = pricingRules.getContent();
			for (PricingRule pricingRuleEntity : pricingRuleEntities) {
				PricingRule pricingRuleDto = new PricingRule();
				BeanUtils.copyProperties(pricingRuleEntity, pricingRuleDto);
				pricingRuleList.add(pricingRuleDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (pricingRuleList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULES, pricingRuleHelper.buildResponseObject(pricingRuleList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, pricingRuleList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to update the PricingRule.
	 * 
	 * @param pricingRuleReqObj:
	 *            PricingRule
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
	public String updatePricingRule(PricingRule pricingRuleReqObj)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<PricingRule> pricingRuleEntity = null;
		try {
			pricingRuleEntity = pricingRuleRepository.findById(pricingRuleReqObj.getId());
			if (pricingRuleEntity == null) {
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}
			pricingRuleReqObj = pricingRuleHelper.populatePricingRule(pricingRuleReqObj, pricingRuleEntity.get());
			pricingRuleRepository.save(pricingRuleReqObj);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.PRICINGRULE, pricingRuleHelper.buildResponseObject(pricingRuleReqObj));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to delete the PricingRule by id.
	 * 
	 * @param pricingRuleId:
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
	public String deletePricingRule(Integer pricingRuleId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		Optional<PricingRule> pricingRule = null;
		try {
			pricingRule = pricingRuleRepository.findById(pricingRuleId);
			if (!pricingRule.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			pricingRuleRepository.delete(pricingRule.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new InvalidParameterException("Pricing Rule could not be deleted.");
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		result = utils.constructSucessJSON(jObj);
		return result;
	}

}
