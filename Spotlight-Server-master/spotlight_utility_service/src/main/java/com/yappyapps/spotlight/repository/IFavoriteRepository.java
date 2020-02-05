package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.Favorite;
import com.yappyapps.spotlight.domain.Viewer;

/**
 * The IFavoriteRepository interface provides the CRUD operations on Favorite domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IFavoriteRepository extends CrudRepository<Favorite, Integer> {

//	/**
//	 * This method is used to find all Favorites with paging and orderBy.
//	 * 
//	 * @param pageable:
//	 *            Pageable
//	 * @return List&lt;Page&gt;
//	 */
//	Page<Favorite> findAll(Pageable pageable);
//
//	/**
//	 * This method is used to find all Favorites orderBy number asc.
//	 * 
//	 * @return List&lt;Favorite&gt;
//	 */
//	List<Favorite> findAllByOrderByNumber();
//
//	/**
//	 * This method is used to find all Favorites by status orderBy number asc.
//	 * 
//	 * @param status:
//	 *            String
//	 * @return List&lt;Favorite&gt;
//	 */
//	List<Favorite> findAllByStatusOrderByNumber(String status);
//
//	/**
//	 * This method is used to find all Favorites by status with paging and orderBy.
//	 * 
//	 * @param status:
//	 *            String
//	 * @param pageable:
//	 *            Pageable
//	 * @return Page&lt;Favorite&gt;
//	 */
//	Page<Favorite> findAllByStatus(String status, Pageable pageable);

	/**
	 * This method is used to find Favorite by BroadcasterInfo and Event and Viewer.
	 * 
	 * @param broadcasterInfo:
	 *            BroadcasterInfo
	 * @param event:
	 *            Event
	 * @param viewer:
	 *            Viewer
	 * @return Favorite;
	 */
	Favorite findByBroadcasterInfoAndEventAndViewer(BroadcasterInfo broadcasterInfo, Event event, Viewer viewer);

	/**
	 * This method is used to find Favorite by Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @return List&lt;Favorite;&gt;
	 */
	List<Favorite> findByViewer(Viewer viewer);

	/**
	 * This method is used to find Favorite by Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;Favorite;&gt;
	 */
	Page<Favorite> findByViewer(Viewer viewer, Pageable pageable);

}
