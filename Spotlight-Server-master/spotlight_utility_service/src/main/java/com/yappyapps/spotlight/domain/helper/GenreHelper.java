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

import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.repository.IGenreRepository;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The GenreHelper class is the utility class to build and validate Genre
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class GenreHelper {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GenreHelper.class);

	/*
	 * IGenreRepository Bean
	 */
	@Autowired
	private IGenreRepository genreRepository;

	/**
	 * This method is used to create the Genre Entity by copying properties from
	 * requested Bean
	 * 
	 * @param genreReqObj
	 *            : Genre
	 * @return Genre: genreEntity
	 * 
	 */
	public Genre populateGenre(Genre genreReqObj) {
		Genre genreEntity = new Genre();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		genreEntity.setName(genreReqObj.getName() != null ? genreReqObj.getName() : null);
		genreEntity.setCreatedOn(currentTime);
		genreEntity.setStatus(genreReqObj.getStatus() != null ? genreReqObj.getStatus() : IConstants.DEFAULT_STATUS);
		genreEntity.setIsCategory(genreReqObj.getIsCategory() != null ? genreReqObj.getIsCategory() : false);

		if (genreReqObj.getGenre() != null) {
			Optional<Genre> parentGenre = genreRepository.findById(genreReqObj.getGenre().getId());
			if (parentGenre.isPresent())
				genreEntity.setGenre(parentGenre.get());
		}
		LOGGER.debug("Genre populated from Requested Genre Object ");
		return genreEntity;
	}

	/**
	 * This method is used to copy the Genre properties from requested Bean to
	 * Entity Bean
	 * 
	 * @param genreReqObj
	 *            : Genre
	 * @param genreEntity
	 *            : Genre
	 * @return Genre: genreEntity
	 * 
	 */
	public Genre populateGenre(Genre genreReqObj, Genre genreEntity) {

		genreEntity.setName(genreReqObj.getName() != null ? genreReqObj.getName() : genreEntity.getName());
		genreEntity.setGenre(genreReqObj.getGenre() != null ? genreReqObj.getGenre() : genreEntity.getGenre());
		genreEntity.setStatus(genreReqObj.getStatus() != null ? genreReqObj.getStatus() : genreEntity.getStatus());
		genreEntity.setIsCategory(genreReqObj.getIsCategory() != null ? genreReqObj.getIsCategory() : genreEntity.getIsCategory());

		if (genreReqObj.getGenre() != null && genreReqObj.getGenre().getName() != null) {
			Optional<Genre> parentGenre = genreRepository.findById(genreReqObj.getGenre().getId());
			if (parentGenre.isPresent())
				genreEntity.setGenre(parentGenre.get());
		}
		LOGGER.debug("Genre Entity populated from Requested Genre Object ");
		return genreEntity;
	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param genre:
	 *            Genre
	 * @return JSONObject: genreObj
	 * 
	 */
	public JSONObject buildResponseObject(Genre genre) throws JSONException {
		JSONObject genreObj = new JSONObject();
		genreObj.put("id", genre.getId());
		genreObj.put("createdOn", genre.getCreatedOn());
		genreObj.put("name", genre.getName());
		genreObj.put("status", genre.getStatus());
		genreObj.put("isCategory", genre.getIsCategory());
		if (genre.getGenre() != null) {
			genreObj.put("genre", new JSONObject().put("id", genre.getGenre().getId()));
		}
		JSONArray childArr = new JSONArray();
		List<Genre> childGenreList = genreRepository.findByGenre(genre);
		for (Genre childGenre : childGenreList) {
			childArr.put(buildResponseObject(childGenre));
		}
		if (childArr.length() > 0)
			genreObj.put(IConstants.CHILDREN, childArr);
		LOGGER.debug("Genre Response Object built for Genre Object id :::: " + genre.getId());
		return genreObj;

	}

	/**
	 * This method is used to build the response object.
	 * 
	 * @param genreList
	 *            : List&lt;Genre&gt;
	 * @return JSONArray: genreArr
	 * 
	 */
	public JSONArray buildResponseObject(List<Genre> genreList) throws JSONException {
		JSONArray genreArr = new JSONArray();
		for (Genre genre : genreList) {
			JSONObject genreObj = buildResponseObject(genre);
			if (genreObj != null && genre.getGenre() == null)
				genreArr.put(genreObj);

		}
		LOGGER.debug("Genre Response Array built with size :::: " + genreArr.length());
		return genreArr;
	}

}
