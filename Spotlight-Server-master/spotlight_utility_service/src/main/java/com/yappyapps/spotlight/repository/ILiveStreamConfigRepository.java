package com.yappyapps.spotlight.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.LiveStreamConfig;

/**
 * The ILiveStreamConfigRepository interface provides the CRUD operations on
 * LiveStreamConfig domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-08-14
 */
@Repository
public interface ILiveStreamConfigRepository extends CrudRepository<LiveStreamConfig, Integer> {

	/**
	 * This method is used to find the LiveStreamConfigs with paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;LiveStreamConfig&gt;
	 */
	Page<LiveStreamConfig> findAll(Pageable pageable);

	LiveStreamConfig findByConnectionType(String connectionType);

}
