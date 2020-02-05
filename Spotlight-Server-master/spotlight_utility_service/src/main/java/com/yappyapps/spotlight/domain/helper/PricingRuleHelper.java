package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.PricingRule;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The PricingRuleHelper class is the utility class to build and validate
 * PricingRule
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class PricingRuleHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PricingRuleHelper.class);

	/**
	 * This method is used to create the PricingRule Entity by copying properties
	 * from requested Bean
	 * 
	 * @param pricingRuleReqObj
	 *            : PricingRule
	 * @return PricingRule: pricingRuleEntity
	 * 
	 */
	public PricingRule populatePricingRule(PricingRule pricingRuleReqObj) {
		PricingRule pricingRuleEntity = new PricingRule();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		pricingRuleEntity.setCreatedOn(currentTime);
		pricingRuleEntity.setDiscountPercentage(
				pricingRuleReqObj.getDiscountPercentage() != null ? pricingRuleReqObj.getDiscountPercentage() : null);
		pricingRuleEntity.setDiscountValidTill(
				pricingRuleReqObj.getDiscountValidTill() != null ? pricingRuleReqObj.getDiscountValidTill() : null);
		pricingRuleEntity.setEventDuration(
				pricingRuleReqObj.getEventDuration() != null ? pricingRuleReqObj.getEventDuration() : null);
		pricingRuleEntity.setMinimumPrice(
				pricingRuleReqObj.getMinimumPrice() != null ? pricingRuleReqObj.getMinimumPrice() : null);
		pricingRuleEntity.setStatus(
				pricingRuleReqObj.getStatus() != null ? pricingRuleReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		pricingRuleEntity.setUpdatedOn(currentTime);
		LOGGER.debug("PricingRule populated from Requested PricingRule Object ");
		return pricingRuleEntity;
	}

	/**
	 * This method is used to copy the PricingRule properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param pricingRuleReqObj
	 *            : PricingRule
	 * @param pricingRuleEntity
	 *            : PricingRule
	 * @return PricingRule: pricingRuleEntity
	 * 
	 */
	public PricingRule populatePricingRule(PricingRule pricingRuleReqObj, PricingRule pricingRuleEntity) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		pricingRuleEntity.setDiscountPercentage(
				pricingRuleReqObj.getDiscountPercentage() != null ? pricingRuleReqObj.getDiscountPercentage()
						: pricingRuleEntity.getDiscountPercentage());
		pricingRuleEntity.setDiscountValidTill(
				pricingRuleReqObj.getDiscountValidTill() != null ? pricingRuleReqObj.getDiscountValidTill()
						: pricingRuleEntity.getDiscountValidTill());
		pricingRuleEntity
				.setEventDuration(pricingRuleReqObj.getEventDuration() != null ? pricingRuleReqObj.getEventDuration()
						: pricingRuleEntity.getEventDuration());
		pricingRuleEntity
				.setMinimumPrice(pricingRuleReqObj.getMinimumPrice() != null ? pricingRuleReqObj.getMinimumPrice()
						: pricingRuleEntity.getMinimumPrice());
		pricingRuleEntity.setStatus(
				pricingRuleReqObj.getStatus() != null ? pricingRuleReqObj.getStatus() : pricingRuleEntity.getStatus());
		pricingRuleEntity.setUpdatedOn(currentTime);
		LOGGER.debug("PricingRule Entity populated from Requested PricingRule Object ");
		return pricingRuleEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param pricingRule:
	 *            PricingRule
	 * @return JSONObject: pricingRuleObj
	 * 
	 */
	public JSONObject buildResponseObject(PricingRule pricingRule) throws JSONException {
		JSONObject pricingRuleObj = new JSONObject();
		pricingRuleObj.put("id", pricingRule.getId());
		pricingRuleObj.put("createdOn", pricingRule.getCreatedOn());
		pricingRuleObj.put("discountPercentage", pricingRule.getDiscountPercentage());
		pricingRuleObj.put("discountValidTill", pricingRule.getDiscountValidTill());
		pricingRuleObj.put("eventDuration", pricingRule.getEventDuration());
		pricingRuleObj.put("minimumPrice", pricingRule.getMinimumPrice());
		pricingRuleObj.put("updatedOn", pricingRule.getUpdatedOn());
		pricingRuleObj.put("status", pricingRule.getStatus());
		LOGGER.debug("PricingRule Response Object built for PricingRule Object id :::: " + pricingRule.getId());
		return pricingRuleObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param pricingRuleList
	 *            : List&lt;PricingRule&gt;
	 * @return JSONArray: pricingRuleArr
	 * 
	 */
	public JSONArray buildResponseObject(List<PricingRule> pricingRuleList) throws JSONException {
		JSONArray pricingRuleArr = new JSONArray();
		for (PricingRule pricingRule : pricingRuleList) {
			pricingRuleArr.put(buildResponseObject(pricingRule));
		}
		LOGGER.debug("PricingRule Response Array built with size :::: " + pricingRuleArr.length());
		return pricingRuleArr;
	}
}
