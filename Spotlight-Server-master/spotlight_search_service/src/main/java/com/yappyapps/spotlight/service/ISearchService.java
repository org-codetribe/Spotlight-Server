package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The ISearchService interface declares all the operations to act upon Genre
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public interface ISearchService {
	/**
	 * This method is used to get all the search results by searchTerm.
	 * 
	 * @param searchTerm:
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
	public String fuzzySearch(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception;
	public String fuzzySearchEventType(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception;
	public String fuzzySearchUpcomingEvent(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get the search result by searchterm Status with paging.
	 * 
	 * @param searchTerm:
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
	public String fuzzySearch(String searchTerm, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;


	/**
	 * This method is used to get all the search results by searchTerm.
	 * 
	 * @param searchTerm:
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
	public String fuzzySearchBroadcasters(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get the search result by searchterm Status with paging.
	 * 
	 * @param searchTerm:
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
	public String fuzzySearchBroadcasters(String searchTerm, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;
	public String fuzzySearchBroadcasters(String searchTerm, Viewer viewer) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the search results by searchTerm.
	 * 
	 * @param searchTerm:
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
	public String fuzzySearchEvents(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get the search result by searchterm Status with paging.
	 * 
	 * @param searchTerm:
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
	public String fuzzySearchEvents(String searchTerm, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

}
