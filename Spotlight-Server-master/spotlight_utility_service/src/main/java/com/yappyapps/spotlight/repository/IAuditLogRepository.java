package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.AuditLog;
import com.yappyapps.spotlight.domain.SpotlightUser;

/**
 * The IAuditLogRepository interface provides the CRUD operations on
 * AuditLog domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IAuditLogRepository extends CrudRepository<AuditLog, Integer> {

	/**
	 * This method is used to find all audit logs by paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;AuditLog&gt;
	 */
	Page<AuditLog> findAll(Pageable pageable);

	/**
	 * This method is used to find all audit logs by spotlightUser.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @return List&lt;AuditLog&gt;
	 */
	List<AuditLog> findBySpotlightUser(SpotlightUser spotlightUser);
}
