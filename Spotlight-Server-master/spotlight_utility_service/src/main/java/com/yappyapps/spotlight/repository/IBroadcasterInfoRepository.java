package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.domain.SpotlightUser;

/**
 * The IBroadcasterInfoRepository interface provides the CRUD operations on
 * BroadcasterInfo domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Rajat Sethi
 * @version 1.0
 * @since 2018-08-04
 */
@Repository
public interface IBroadcasterInfoRepository extends CrudRepository<BroadcasterInfo, Integer> {

	/**
	 * This method is used to find all BroadcasterInfo with paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;BroadcasterInfo&gt;
	 */
	Page<BroadcasterInfo> findAll(Pageable pageable);

	/**
	 * This method is used to find all BroadcasterInfo by isTrending.
	 * 
	 * @param isTrending:
	 *            Boolean
	 * @return List&lt;BroadcasterInfo&gt;
	 */
	List<BroadcasterInfo> findByIsTrending(Boolean isTrending);

	/**
	 * This method is used to find all BroadcasterInfo by isTrending with paging.
	 * 
	 * @param isTrending:
	 *            Boolean
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;BroadcasterInfo&gt;
	 */
	Page<BroadcasterInfo> findByIsTrending(Boolean isTrending, Pageable pageable);

	/**
	 * This method is used to find all BroadcasterInfo by status and isTrending.
	 * 
	 * @param status:
	 *            String
	 * @param isTrending:
	 *            Boolean
	 * @return List&lt;BroadcasterInfo&gt;
	 */
	List<BroadcasterInfo> findByStatusAndIsTrending(String status, Boolean isTrending);

	/**
	 * This method is used to find all BroadcasterInfo by status and isTrending with
	 * paging.
	 * 
	 * @param status:
	 *            String
	 * @param isTrending:
	 *            Boolean
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;BroadcasterInfo&gt;
	 */
	Page<BroadcasterInfo> findByStatusAndIsTrending(String status, Boolean isTrending, Pageable pageable);

	/**
	 * This method is used to find all BroadcasterInfo by status.
	 * 
	 * @param status:
	 *            String
	 * @return List&lt;BroadcasterInfo&gt;
	 */
	List<BroadcasterInfo> findByStatus(String status);

	/**
	 * This method is used to find all BroadcasterInfo by status with
	 * paging.
	 * 
	 * @param status:
	 *            String
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;BroadcasterInfo&gt;
	 */
	Page<BroadcasterInfo> findByStatus(String status, Pageable pageable);
	
	/**
	 * This method is used to find all BroadcasterInfo by Genre.
	 * 
	 * @param genre:
	 *            Genre
	 * @return List&lt;BroadcasterInfo&gt;
	 */
	List<BroadcasterInfo> findByGenre(Genre genre);

	/**
	 * This method is used to find all BroadcasterInfo by Genre with paging.
	 * 
	 * @param genre:
	 *            Genre
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;BroadcasterInfo&gt;
	 */
	Page<BroadcasterInfo> findByGenre(Genre genre, Pageable pageable);

	/**
	 * This method is used to find the BroadcasterInfo by SpotlightUser.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @return BroadcasterInfo
	 */
	BroadcasterInfo findBySpotlightUser(SpotlightUser spotlightUser);
}
