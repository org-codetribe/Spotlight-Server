package com.yappyapps.spotlight.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IGenreService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Utils;

/**
 * The GenreController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on Genre.
 * 
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/genre")
public class GenreController {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GenreController.class);

	/**
	 * Controller Name.
	 */
	private static final String controller = "Genre";

	/**
	 * IGenreService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IGenreService genreService;

	/**
	 * MeteringService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private MeteringService meteringService;

	/**
	 * Gson dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Gson gson;

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * This method is used to expose the REST API as POST to create Genre.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Created genre in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws AlreadyExistException
	 *             AlreadyExistException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String createGenre(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, AlreadyExistException, BusinessException {
		String operation = "createGenre";
		LOGGER.info("GenreController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		Genre genre = gson.fromJson(requestBody, Genre.class);

		utils.isEmptyOrNull(genre.getName(), "Name");
		utils.isStatusValid(genre.getStatus());
		try {
			result = genreService.createGenre(genre);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (AlreadyExistException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime),
					requestBody.length());
		}

		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get all Genres with
	 * paging.
	 * 
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Genres in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getAllGenres(@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getAllGenres";
		LOGGER.info("GenreController :: " + operation + " :: limit :: " + limit + " :: offset :: " + offset
				+ " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, Genre.class);

				result = genreService.getAllGenres(Integer.valueOf(limit), Integer.valueOf(offset), direction, orderBy);
			} else {
				result = genreService.getAllGenres();
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get Genre by Id.
	 * 
	 * @param genreId:
	 *            String
	 * @return ResponseBody: Genre in JSON format
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{genreId}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getGenreById(@PathVariable("genreId") String genreId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getGenreById";
		LOGGER.info("GenreController :: " + operation + " :: genreId :: " + genreId);
		long startTime = System.currentTimeMillis();

		String result = "";
		utils.isEmptyOrNull(genreId, "genreId");
		utils.isIntegerGreaterThanZero(genreId, "genreId");
		try {
			result = genreService.getGenre(Integer.parseInt(genreId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as GET to get all Genres by status.
	 * 
	 * @param status:
	 *            String
	 * @param limit:
	 *            String
	 * @param offset:
	 *            String
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * @return ResponseBody: Genres in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/status/{status}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String getGenresByStatus(@PathVariable("status") String status,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "direction", required = false) String direction,
			@RequestParam(value = "orderBy", required = false) String orderBy)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "getGenresByStatus";
		LOGGER.info("GenreController :: " + operation + " :: status :: " + status + " :: limit :: " + limit
				+ " :: offset :: " + offset + " :: direction :: " + direction + " :: orderBy :: " + orderBy);
		long startTime = System.currentTimeMillis();
		String result = null;
		utils.isEmptyOrNull(status, "Status");
		utils.isStatusValid(status);
		try {
			if (offset != null && limit != null) {
				utils.isInteger(offset, "offset");
				utils.isInteger(limit, "limit");
				utils.isOrderByDirectionValid(direction);
				utils.isOrderByPropertyValid(orderBy, Genre.class);

				result = genreService.getGenresByStatus(status, Integer.valueOf(limit), Integer.valueOf(offset),
						direction, orderBy);
			} else {
				result = genreService.getGenresByStatus(status);
			}
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as PUT to update Genre.
	 * 
	 * @param requestBody:
	 *            Request Body in JSON Format.
	 * @param contentType:
	 *            "application/json"
	 * @return ResponseBody: Updated Genre in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String updateGenre(@RequestBody String requestBody,
			@RequestHeader("Content-Type") String contentType)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "updateGenre";
		LOGGER.info("GenreController :: " + operation + " :: RequestBody :: " + requestBody + " :: contentType :: "
				+ contentType);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isBodyJSONObject(requestBody);
		Genre genre = gson.fromJson(requestBody, Genre.class);
		utils.isIntegerGreaterThanZero(genre.getId(), "Id");
		utils.isAvailableObjectEmpty(genre.getName(), "Name");
		try {
			result = genreService.updateGenre(genre);
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

	/**
	 * This method is used to expose the REST API as DELETE to delete Genre by Id.
	 * 
	 * @param genreId:
	 *            String
	 * @return ResponseBody: Response in JSON format.
	 * 
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 */
	@RequestMapping(value = "/id/{genreId}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody String deleteGenre(@PathVariable("genreId") String genreId)
			throws InvalidParameterException, ResourceNotFoundException, BusinessException {
		String operation = "deleteGenre";
		LOGGER.info("GenreController :: " + operation + " :: genreId :: " + genreId);
		long startTime = System.currentTimeMillis();
		String result = "";
		utils.isEmptyOrNull(genreId, "genreId");
		utils.isIntegerGreaterThanZero(genreId, "genreId");
		try {
			result = genreService.deleteGenre(Integer.parseInt(genreId));
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
		} finally {
			meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
		}
		return result;
	}

}
