package com.yappyapps.spotlight.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.SpotlightCommission;

/**
 * The ISpotlightCommissionRepository interface provides the CRUD operations on
 * SpotlightCommission domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface ISpotlightCommissionRepository extends CrudRepository<SpotlightCommission, Integer> {

	/**
	 * This method is used to find all SpotlightCommissions with paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;SpotlightCommission&gt;
	 */
	Page<SpotlightCommission> findAll(Pageable pageable);

	/**
	 * This method is used to find SpotlightCommissions by BroadcasterInfo and
	 * Event.
	 * 
	 * @param broadcasterInfo:
	 *            BroadcasterInfo
	 * @param event:
	 *            Event
	 * @return SpotlightCommission
	 */
	SpotlightCommission findByBroadcasterInfoAndEvent(BroadcasterInfo broadcasterInfo, Event event);
	
	/**
	 * This method is used to find SpotlightCommissions by BroadcasterInfo and
	 * Event.
	 * 
	 * @param event:
	 *            Event
	 * @return SpotlightCommission
	 */
	SpotlightCommission findByEvent(Event event);

}
