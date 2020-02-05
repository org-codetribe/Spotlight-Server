package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.SpotlightUser;

/**
 * The ISpotlightUserRepository interface provides the CRUD operations on
 * SpotlightUser domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface ISpotlightUserRepository extends CrudRepository<SpotlightUser, Integer> {

	/**
	 * This method is used to find all SpotlightUsers with paging and orderBy.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;SpotlightUser&gt;
	 */
	Page<SpotlightUser> findAll(Pageable pageable);

	/**
	 * This method is used to find all SpotlightUsers by userType.
	 * 
	 * @param userType:
	 *            String
	 * @return List&lt;SpotlightUser&gt;
	 */
	List<SpotlightUser> findAllByUserType(String userType);

	/**
	 * This method is used to find all SpotlightUsers by userType with paging and orderBy.
	 * 
	 * @param userType:
	 *            String
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;SpotlightUser&gt;
	 */
	Page<SpotlightUser> findAllByUserType(String userType, Pageable pageable);

	/**
	 * This method is used to find SpotlightUser by username.
	 * 
	 * @param username:
	 *            String
	 * @return SpotlightUser
	 */
	SpotlightUser findByUsername(String username);

	/**
	 * This method is used to find SpotlightUser by email.
	 * 
	 * @param email:
	 *            String
	 * @return SpotlightUser
	 */
	SpotlightUser findByEmail(String email);

	/**
	 * This method is used to find SpotlightUser by AccessRole.
	 * 
	 * @param roleName:
	 *            String
	 * @return List&lt;SpotlightUser&gt;
	 */
	List<SpotlightUser> findAllByRoles(String roleName);

	/**
	 * This method is used to find SpotlightUser by phone.
	 * 
	 * @param phone:
	 *            String
	 * @return SpotlightUser
	 */
	SpotlightUser findByPhone(String phone);

	/**
	 * This method is used to find SpotlightUser by email or phone.
	 * 
	 * @param email:
	 *            String
	 * @param phone:
	 *            String
	 * @return List&lt;SpotlightUser&gt;
	 */
	List<SpotlightUser> findByEmailOrPhone(String email, String phone);

}
