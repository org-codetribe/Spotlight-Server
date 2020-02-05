package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.domain.CouponConsumption;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.repository.ICouponConsumptionRepository;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The CouponHelper class is the utility class to build and validate Coupon
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class CouponHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CouponHelper.class);

	/*
	 * IEventRepository Bean
	 */
	@Autowired
	private IEventRepository eventRepository;

	/*
	 * ICouponConsumptionRepository Bean
	 */
	@Autowired
	private ICouponConsumptionRepository couponConsumptionRepository;
	
	/*
	 * ViewerHelper Bean
	 */
	@Autowired
	private ViewerHelper viewerHelper;

	/**
	 * This method is used to create the Coupon Entity by copying properties from
	 * requested Bean
	 * 
	 * @param couponReqObj
	 *            : Coupon
	 * @return Coupon: couponEntity
	 * 
	 */
	public Coupon populateCoupon(Coupon couponReqObj) {
		Coupon couponEntity = new Coupon();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		couponEntity.setNumber(Utils.generateRandomString(12));
		couponEntity.setRedemptionLimit(couponReqObj.getRedemptionLimit() != null ? couponReqObj.getRedemptionLimit() : 1);
		couponEntity.setCreatedOn(currentTime);
		couponEntity.setStatus(couponReqObj.getStatus() != null ? couponReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		couponEntity.setType(couponReqObj.getType() != null ? couponReqObj.getType() : null);
		couponEntity.setUniqueName(Utils.generateRandomString(32));

		if (couponReqObj.getEvent() != null) {
			Optional<Event> event = eventRepository.findById(couponReqObj.getEvent().getId());
			if (event.isPresent())
				couponEntity.setEvent(event.get());
		}
		LOGGER.debug("Coupon populated from Requested Coupon Object ");
		return couponEntity;
	}

	/**
	 * This method is used to copy the Coupon properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param couponReqObj
	 *            : Coupon
	 * @param couponEntity
	 *            : Coupon
	 * @return Coupon: couponEntity
	 * 
	 */
	public Coupon populateCoupon(Coupon couponReqObj, Coupon couponEntity) {

//		couponEntity.setNumber(couponReqObj.getNumber() != null ? couponReqObj.getNumber() : couponEntity.getNumber());
		couponEntity.setRedemptionLimit(couponReqObj.getRedemptionLimit() != null ? couponReqObj.getRedemptionLimit() : couponEntity.getRedemptionLimit());
		couponEntity.setStatus(couponReqObj.getStatus() != null ? couponReqObj.getStatus() : couponEntity.getStatus());
		couponEntity.setType(couponReqObj.getType() != null ? couponReqObj.getType() : couponEntity.getType());
//		if (couponReqObj.getBroadcasterInfo() != null) {
//			Optional<BroadcasterInfo> broadcasterInfo = broadcasterInfoRepository.findById(couponReqObj.getBroadcasterInfo().getId());
//			if (broadcasterInfo.isPresent())
//				couponEntity.setBroadcasterInfo(broadcasterInfo.get());
//		}
		if (couponReqObj.getEvent() != null) {
			Optional<Event> event = eventRepository.findById(couponReqObj.getEvent().getId());
			if (event.isPresent())
				couponEntity.setEvent(event.get());
		}

		LOGGER.debug("Coupon Entity populated from Requested Coupon Object ");
		return couponEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param coupon:
	 *            Coupon
	 * @return JSONObject: couponObj
	 * 
	 */
	public JSONObject buildResponseObject(Coupon coupon) throws JSONException {
		JSONObject couponObj = new JSONObject();
		couponObj.put("id", coupon.getId());
		couponObj.put("createdOn", coupon.getCreatedOn());
		couponObj.put("number", coupon.getNumber());
		couponObj.put("redemptionLimit", coupon.getRedemptionLimit());
		couponObj.put("status", coupon.getStatus());
		couponObj.put("uniqueName", coupon.getUniqueName());
		couponObj.put("type", coupon.getType());
		if(coupon.getEvent() != null)
			couponObj.put("event", new JSONObject().put("id", coupon.getEvent().getId()));
		List<CouponConsumption> couponConsumption = couponConsumptionRepository.findByCoupon(coupon);
		boolean couponRedeemed = (couponConsumption != null && couponConsumption.size() > 0) ? true : false;
		couponObj.put("couponRedeemed", couponRedeemed);
		if(couponRedeemed) {
			if(couponConsumption.size() == 1) {
				couponObj.put("viewer", viewerHelper.buildResponseObject(couponConsumption.get(0).getViewer()));
			}
			else if(couponConsumption.size() > 1) {
				JSONArray viewerArr =  new JSONArray();
				for(int i = 0; i < couponConsumption.size(); i++) {
					viewerArr.put(viewerHelper.buildResponseObject(couponConsumption.get(i).getViewer()));
				}
				couponObj.put("viewer", viewerArr);
			}
				
		}

		LOGGER.debug("Coupon Response Object built for Coupon Object id :::: " + coupon.getId());
		return couponObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param couponList
	 *            : List&lt;Coupon&gt;
	 * @return JSONArray: couponArr
	 * 
	 */
	public JSONArray buildResponseObject(List<Coupon> couponList) throws JSONException {
		JSONArray couponArr = new JSONArray();
		for (Coupon coupon : couponList) {
			JSONObject couponObj = buildResponseObject(coupon);
			if (couponObj != null)
				couponArr.put(couponObj);

		}
		LOGGER.debug("Coupon Response Array built with size :::: " + couponArr.length());
		return couponArr;
	}

}
