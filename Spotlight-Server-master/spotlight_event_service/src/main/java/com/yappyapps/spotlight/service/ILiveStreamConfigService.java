package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.LiveStreamConfig;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The ILiveStreamConfigService interface declares all the operations to act
 * upon LiveStreamConfig
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
public interface ILiveStreamConfigService {
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

	public String createLiveStreamConfig(LiveStreamConfig liveStreamConfigReqObj)
			throws AlreadyExistException, BusinessException, Exception;

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

	public String getAllLiveStreamConfigs() throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getAllLiveStreamConfigs(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

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

	public String getLiveStreamConfig(Integer liveStreamConfigId)
			throws ResourceNotFoundException, BusinessException, Exception;

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

	public String updateLiveStreamConfig(LiveStreamConfig liveStreamConfigReqObj)
			throws ResourceNotFoundException, BusinessException, Exception;

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

	public String deleteLiveStreamConfig(Integer liveStreamConfigId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;

}
