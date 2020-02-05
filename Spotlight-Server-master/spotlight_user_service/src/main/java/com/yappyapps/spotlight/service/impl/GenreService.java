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

import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.domain.helper.GenreHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IGenreRepository;
import com.yappyapps.spotlight.service.IGenreService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The GenreService class is the implementation of IGenreService
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class GenreService implements IGenreService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GenreService.class);

	/**
	 * IGenreRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IGenreRepository genreRepository;

	/**
	 * GenreHelper dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private GenreHelper genreHelper;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

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
	@Override
	public String createGenre(Genre genreReqObj) throws AlreadyExistException, BusinessException, Exception {
		String result = null;
		

		if ((genreRepository.findByName(genreReqObj.getName()) != null)) {
			throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
		}
		Genre genreEntity = null;
		try {
			genreEntity = genreHelper.populateGenre(genreReqObj);
			genreEntity = genreRepository.save(genreEntity);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRE, genreHelper.buildResponseObject(genreEntity));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getAllGenres() throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		List<Genre> genreEntityList = null;
		try {
			genreEntityList = (List<Genre>) genreRepository.findAllByOrderByName();
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (genreEntityList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRES, genreHelper.buildResponseObject(genreEntityList));

		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getAllGenres(Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<Genre> genreList = new ArrayList<Genre>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction)
					: Direction.valueOf(IConstants.DEFAULT_ORDERBY_DIRECTION));
			orderBy = (orderBy != null ? orderBy : IConstants.DEFAULT_ORDERBY);
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<Genre> genres = genreRepository.findAll(pageableRequest);
			totalCount = genres.getTotalElements();
			List<Genre> genreEntities = genres.getContent();
			for (Genre genreEntity : genreEntities) {
				Genre genreDto = new Genre();
				BeanUtils.copyProperties(genreEntity, genreDto);
				genreList.add(genreDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (genreList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRES, genreHelper.buildResponseObject(genreList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, genreList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getGenre(Integer genreId) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		Optional<Genre> genre = null;
		
		try {
			genre = genreRepository.findById(genreId);
			if (!genre.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRE, genreHelper.buildResponseObject(genre.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

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
	@Override
	public String getGenresByStatus(String status) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		List<Genre> genreList = null;
		
		try {
			genreList = genreRepository.findAllByStatusOrderByName(status);
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (genreList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRES, genreHelper.buildResponseObject(genreList));
		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String getGenresByStatus(String status, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		long totalCount = 0;
		
		List<Genre> genreList = new ArrayList<Genre>();
		int pageNum = offset / limit;
		try {
			Direction directionObj = (direction != null ? Direction.valueOf(direction)
					: Direction.valueOf(IConstants.DEFAULT_ORDERBY_DIRECTION));
			orderBy = (orderBy != null ? orderBy : IConstants.DEFAULT_ORDERBY);
			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
			Page<Genre> genres = genreRepository.findAllByStatus(status, pageableRequest);
			totalCount = genres.getTotalElements();
			List<Genre> genreEntities = genres.getContent();
			for (Genre genreEntity : genreEntities) {
				Genre genreDto = new Genre();
				BeanUtils.copyProperties(genreEntity, genreDto);
				genreList.add(genreDto);
			}
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		if (genreList.size() <= 0) {
			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRES, genreHelper.buildResponseObject(genreList));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, genreList.size());

		result = utils.constructSucessJSON(jObj);

		return result;

	}

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
	@Override
	public String updateGenre(Genre genreReqObj) throws ResourceNotFoundException, BusinessException, Exception {
		String result = null;
		
		Optional<Genre> genreEntity = null;
		try {
			genreEntity = genreRepository.findById(genreReqObj.getId());
			if (!genreEntity.isPresent()) {
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
			}

			genreHelper.populateGenre(genreReqObj, genreEntity.get());
			genreRepository.save(genreEntity.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new Exception(sqlException.getMessage());
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}

		JSONObject jObj = new JSONObject();
		jObj.put(IConstants.GENRE, genreHelper.buildResponseObject(genreEntity.get()));
		result = utils.constructSucessJSON(jObj);
		return result;
	}

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
	@Override
	public String deleteGenre(Integer genreId)
			throws ResourceNotFoundException, InvalidParameterException, BusinessException, Exception {
		String result = null;
		Optional<Genre> genre = null;
		try {
			genre = genreRepository.findById(genreId);
			if (!genre.isPresent())
				throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

			genreRepository.delete(genre.get());
		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
			throw new InvalidParameterException("Genre could not be deleted as it has Broadcasters.");
		} catch (HibernateException | JpaSystemException sqlException) {
			throw new Exception(sqlException.getMessage());
		}
		JSONObject jObj = new JSONObject();
		result = utils.constructSucessJSON(jObj);
		return result;
	}

}
