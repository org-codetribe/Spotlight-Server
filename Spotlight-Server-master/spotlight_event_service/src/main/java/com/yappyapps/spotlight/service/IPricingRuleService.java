package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.PricingRule;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The IPricingRuleService interface declares all the operations to act upon
 * PricingRule
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
public interface IPricingRuleService {
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

	public String createPricingRule(PricingRule pricingRuleReqObj)
			throws AlreadyExistException, BusinessException, Exception;

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

	public String getAllPricingRules() throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getAllPricingRules(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getPricingRule(Integer pricingRuleId) throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getPricingRuleByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getPricingRuleByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

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

	public String updatePricingRule(PricingRule pricingRuleReqObj)
			throws ResourceNotFoundException, BusinessException, Exception;

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

	public String deletePricingRule(Integer pricingRuleId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;

}
