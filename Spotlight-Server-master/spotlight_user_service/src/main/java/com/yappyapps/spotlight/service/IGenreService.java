package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;

/**
 * The IGenreService interface declares all the operations to act upon Genre
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public interface IGenreService {
	/**
	 * This method is used to create the Genre
	 * 
	 * @param genreReqObj:
	 *            Genre
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
	public String createGenre(Genre genreReqObj) throws AlreadyExistException, BusinessException, Exception;

	/**
	 * This method is used to get all Genre.
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
	public String getAllGenres() throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all Genres with paging.
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
	public String getAllGenres(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get Genre by genreId.
	 * 
	 * @param genreId:
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
	public String getGenre(Integer genreId) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Genres by Status.
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
	public String getGenresByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to get all the Genres by Status with paging.
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
	public String getGenresByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to update the Genre.
	 * 
	 * @param genreReqObj:
	 *            Genre
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
	public String updateGenre(Genre genreReqObj) throws ResourceNotFoundException, BusinessException, Exception;

	/**
	 * This method is used to delete the Genre by id.
	 * 
	 * @param genreId:
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
	public String deleteGenre(Integer genreId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception;
}
