package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.Genre;

/**
 * The IGenreRepository interface provides the CRUD operations on Genre domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IGenreRepository extends CrudRepository<Genre, Integer> {

	/**
	 * This method is used to find all Genres with paging and orderBy.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return List&lt;Page&gt;
	 */
	Page<Genre> findAll(Pageable pageable);

	/**
	 * This method is used to find Genre by name.
	 * 
	 * @param name:
	 *            String
	 * @return Genre;
	 */
	Genre findByName(String name);

	/**
	 * This method is used to find all Genres by parent Genre.
	 * 
	 * @param genre:
	 *            Genre
	 * @return List&lt;Genre&gt;
	 */
	List<Genre> findByGenre(Genre genre);

	/**
	 * This method is used to find all Genres orderBy name asc.
	 * 
	 * @return List&lt;Genre&gt;
	 */
	List<Genre> findAllByOrderByName();

	/**
	 * This method is used to find all Genres by status orderBy name asc.
	 * 
	 * @param status:
	 *            String
	 * @return List&lt;Genre&gt;
	 */
	List<Genre> findAllByStatusOrderByName(String status);

	/**
	 * This method is used to find all Genres by status with paging and orderBy.
	 * 
	 * @param status:
	 *            String
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;Genre&gt;
	 */
	Page<Genre> findAllByStatus(String status, Pageable pageable);

	List<Genre> findByIsCategory(Boolean isCategory);

}
