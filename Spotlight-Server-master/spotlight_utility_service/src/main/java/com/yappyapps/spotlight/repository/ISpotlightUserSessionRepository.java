package com.yappyapps.spotlight.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.SpotlightUserSession;

/**
 * The ISpotlightUserSessionRepository interface provides the CRUD operations on SpotlightUserSession domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface ISpotlightUserSessionRepository extends CrudRepository<SpotlightUserSession, Integer> {

	SpotlightUserSession findBySpotlightUser(SpotlightUser spotlightUser);

	@Transactional
	void deleteByAuthToken(String authToken);

	@Transactional
	void deleteBySpotlightUser(SpotlightUser spotlightUser);

	SpotlightUserSession findByAuthToken(String authToken);

}
