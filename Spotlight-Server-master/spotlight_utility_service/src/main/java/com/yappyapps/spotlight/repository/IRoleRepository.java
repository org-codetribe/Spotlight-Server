package com.yappyapps.spotlight.repository;

import com.yappyapps.spotlight.domain.AccessRole;
import com.yappyapps.spotlight.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
public interface IRoleRepository extends CrudRepository<Role, Integer> {

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
	Page<Role> findAll(Pageable pageable);
}
