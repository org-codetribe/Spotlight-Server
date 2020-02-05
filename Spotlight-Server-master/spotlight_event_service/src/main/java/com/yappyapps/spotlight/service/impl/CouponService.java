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

import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.helper.CouponHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.ICouponRepository;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.service.ICouponService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The CouponService class is the implementation of ICouponService
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class CouponService implements ICouponService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CouponService.class);

	/**
	 * ICouponRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ICouponRepository couponRepository;

	/**
	 * IEventRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IEventRepository eventRepository;
	
	/**
	 * CouponHelper dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private CouponHelper couponHelper;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * This method is used to create the Coupon
	 * 
	 * @param couponReqObj:
	 *            Coupon
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
	public String createCoupon(Coupon couponReqObj) throws AlreadyExistException, BusinessException, Exception {
		String result = null;

		Coupon couponEntity = null;
		List<Coupon> couponEntityList = new ArrayList<>();
		try {
			Optional<Event> eventEntity = eventRepository.findById(couponReqObj.getEvent().getId());
			if(!eventEntity.isPresent()) {
				throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}
			if(couponReqObj.getCount() != null) {
				couponReqObj.setRedemptionLimit(1);
			} else {
				couponReqObj.setCount(1);
			}
			for(int i = 0; i < couponReqObj.getCount(); i++) {
				couponEntity = couponHelper.populateCoupon(couponReqObj);
				couponEntity = couponRepository.save(couponEntity);
			}
			
			couponEntityList = couponRepository.findByEvent(eventEntity.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPONS, couponHelper.buildResponseObject(couponEntityList));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all Coupon.
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
	public String getAllCoupons() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<Coupon> couponEntityList = new ArrayList<>();
		try {
			couponEntityList = (List<Coupon>) couponRepository.findAllByOrderByNumber();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

//		if (couponEntityList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPONS, couponHelper.buildResponseObject(couponEntityList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all Coupons with paging.
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
	public String getAllCoupons(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<Coupon> couponList = new ArrayList<Coupon>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction)
					: Direction.valueOf(IConstants.DEFAULT_ORDERBY_DIRECTION));
			orderBy = (orderBy != null ? orderBy : IConstants.DEFAULT_ORDERBY);
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<Coupon> coupons = couponRepository.findAll(pageableRequest);
			totalCount = coupons.getTotalElements();
			List<Coupon> couponEntities = coupons.getContent();
			for (Coupon couponEntity : couponEntities) {
				Coupon couponDto = new Coupon();
				BeanUtils.copyProperties(couponEntity, couponDto);
				couponList.add(couponDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

//		if (couponList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPONS, couponHelper.buildResponseObject(couponList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, couponList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get Coupon by couponId.
	 * 
	 * @param couponId:
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
	public String getCoupon(Integer couponId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<Coupon> coupon = null;
		
		try {
			coupon = couponRepository.findById(couponId);
			if (!coupon.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPON, couponHelper.buildResponseObject(coupon.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to get all the Coupons by Status.
	 * 
	 * @param status:
	 *            String
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
	public String getCouponsByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		List<Coupon> couponList =  new ArrayList<>();
		
		try {
			couponList = couponRepository.findAllByStatusOrderByNumber(status);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

//		if (couponList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPONS, couponHelper.buildResponseObject(couponList));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all the Coupons by Status with paging.
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
	public String getCouponsByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<Coupon> couponList = new ArrayList<Coupon>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction)
					: Direction.valueOf(IConstants.DEFAULT_ORDERBY_DIRECTION));
			orderBy = (orderBy != null ? orderBy : IConstants.DEFAULT_ORDERBY);
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<Coupon> coupons = couponRepository.findAllByStatus(status, pageableRequest);
			totalCount = coupons.getTotalElements();
			List<Coupon> couponEntities = coupons.getContent();
			for (Coupon couponEntity : couponEntities) {
				Coupon couponDto = new Coupon();
				BeanUtils.copyProperties(couponEntity, couponDto);
				couponList.add(couponDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

//		if (couponList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPONS, couponHelper.buildResponseObject(couponList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, couponList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all the Coupons by Event.
	 * 
	 * @param eventId:
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
	public String getCouponsByEvent(Integer eventId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		List<Coupon> couponList = new ArrayList<>();
		try {
			Optional<Event> eventEntity = eventRepository.findById(eventId);
			if(!eventEntity.isPresent()) {
				throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}
			couponList = couponRepository.findByEvent(eventEntity.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

//		if (couponList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPONS, couponHelper.buildResponseObject(couponList));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to update the Coupon.
	 * 
	 * @param couponReqObj:
	 *            Coupon
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
	public String updateCoupon(Coupon couponReqObj) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<Coupon> couponEntity = null;
		try {
			couponEntity = couponRepository.findById(couponReqObj.getId());
			if (!couponEntity.isPresent()) {
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}

			couponHelper.populateCoupon(couponReqObj, couponEntity.get());
			couponRepository.save(couponEntity.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.COUPON, couponHelper.buildResponseObject(couponEntity.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to delete the Coupon by id.
	 * 
	 * @param couponId:
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
	public String deleteCoupon(Integer couponId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		Optional<Coupon> coupon = null;
		try {
			coupon = couponRepository.findById(couponId);
			if (!coupon.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			couponRepository.delete(coupon.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new InvalidParameterException("Coupon could not be deleted.");
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		result = utils.constructSucessJSON(jObj);
		return result;
	}

}
