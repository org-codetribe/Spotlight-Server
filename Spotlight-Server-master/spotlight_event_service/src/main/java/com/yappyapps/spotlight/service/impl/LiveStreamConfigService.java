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

import com.yappyapps.spotlight.domain.LiveStreamConfig;
import com.yappyapps.spotlight.domain.helper.LiveStreamConfigHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.ILiveStreamConfigRepository;
import com.yappyapps.spotlight.service.ILiveStreamConfigService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The LiveStreamConfigService class is the implementation of
 * ILiveStreamConfigService
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Service
public class LiveStreamConfigService implements ILiveStreamConfigService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveStreamConfigService.class);

	/**
	 * ILiveStreamConfigRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ILiveStreamConfigRepository liveStreamConfigRepository;

	/**
	 * LiveStreamConfigHelper dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private LiveStreamConfigHelper liveStreamConfigHelper;
	
	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * This method is used to create the LiveStreamConfig
	 * 
	 * @param liveStreamConfigReqObj:
	 *            LiveStreamConfig
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
	public String createLiveStreamConfig(LiveStreamConfig liveStreamConfigReqObj)
			throws AlreadyExistException, BusinessException, Exception {
		String result = null;
		

		LiveStreamConfig liveStreamConfigEntity = new LiveStreamConfig();
		try {
			liveStreamConfigEntity = liveStreamConfigHelper.populateLiveStreamConfig(liveStreamConfigReqObj);
			liveStreamConfigEntity = liveStreamConfigRepository.save(liveStreamConfigEntity);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.LIVESTREAMCONFIG, liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntity));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all LiveStreamConfigs.
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
	public String getAllLiveStreamConfigs() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<LiveStreamConfig> liveStreamConfigEntityList = null;
		try {
			liveStreamConfigEntityList = (List<LiveStreamConfig>) liveStreamConfigRepository.findAll();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (liveStreamConfigEntityList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.LIVESTREAMCONFIGS, liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntityList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all LiveStreamConfigs with paging.
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
	public String getAllLiveStreamConfigs(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<LiveStreamConfig> liveStreamConfigEntityList = new ArrayList<LiveStreamConfig>();
		int pageNum = offset / limit;

		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
			orderBy = (orderBy != null ? orderBy : "id");
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<LiveStreamConfig> liveStreamConfigs = liveStreamConfigRepository.findAll(pageableRequest);
			totalCount = liveStreamConfigs.getTotalElements();
			List<LiveStreamConfig> liveStreamConfigEntities = liveStreamConfigs.getContent();
			for (LiveStreamConfig liveStreamConfigEntity : liveStreamConfigEntities) {
				LiveStreamConfig liveStreamConfigDto = new LiveStreamConfig();
				BeanUtils.copyProperties(liveStreamConfigEntity, liveStreamConfigDto);
				liveStreamConfigEntityList.add(liveStreamConfigDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (liveStreamConfigEntityList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.LIVESTREAMCONFIGS, liveStreamConfigHelper.buildResponseObject(liveStreamConfigEntityList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, liveStreamConfigEntityList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

	/**
	 * This method is used to get all LiveStreamConfig by id.
	 * 
	 * @param liveStreamConfigId:
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
	public String getLiveStreamConfig(Integer liveStreamConfigId)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<LiveStreamConfig> liveStreamConfig = null;
		
		try {
			liveStreamConfig = liveStreamConfigRepository.findById(liveStreamConfigId);
			if (!liveStreamConfig.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.LIVESTREAMCONFIG, liveStreamConfigHelper.buildResponseObject(liveStreamConfig.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to update the LiveStreamConfig.
	 * 
	 * @param liveStreamConfigReqObj:
	 *            LiveStreamConfig
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
	public String updateLiveStreamConfig(LiveStreamConfig liveStreamConfigReqObj)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<LiveStreamConfig> liveStreamConfigEntity = null;
		try {
			liveStreamConfigEntity = liveStreamConfigRepository.findById(liveStreamConfigReqObj.getId());
			if (liveStreamConfigEntity == null) {
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}
			liveStreamConfigReqObj = liveStreamConfigHelper.populateLiveStreamConfig(liveStreamConfigReqObj,
					liveStreamConfigEntity.get());
			liveStreamConfigRepository.save(liveStreamConfigReqObj);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.LIVESTREAMCONFIG, liveStreamConfigHelper.buildResponseObject(liveStreamConfigReqObj));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

	/**
	 * This method is used to delete the LiveStreamConfig by id.
	 * 
	 * @param liveStreamConfigId:
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
	public String deleteLiveStreamConfig(Integer liveStreamConfigId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		Optional<LiveStreamConfig> liveStreamConfig = null;
		try {
			liveStreamConfig = liveStreamConfigRepository.findById(liveStreamConfigId);
			if (!liveStreamConfig.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			liveStreamConfigRepository.delete(liveStreamConfig.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new InvalidParameterException("LiveStreamConfig could not be deleted.");
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		result = utils.constructSucessJSON(jObj);
		return result;
	}

}
