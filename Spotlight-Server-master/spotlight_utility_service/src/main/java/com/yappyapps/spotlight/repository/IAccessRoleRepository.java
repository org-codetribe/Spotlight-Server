package com.yappyapps.spotlight.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.AccessRole;

/**
 * The IAccessRoleRepository interface provides the CRUD operations on
 * AccessRole domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IAccessRoleRepository extends CrudRepository<AccessRole, Integer> {

	/**
	 * This method is used to check if access roles based on roleName exists or not.
	 * 
	 * @param roleName:
	 *            String
	 * @return true/false: boolean
	 */
	boolean existsRoleByName(String roleName);

	/**
	 * This method is used to find all access roles by paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;AccessRole&gt;
	 */
	Page<AccessRole> findAll(Pageable pageable);
}
