package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.Viewer;

/**
 * The IViewerRepository interface provides the CRUD operations on Viewer domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IViewerRepository extends JpaRepository<Viewer, Integer> {

	/**
	 * This method is used to find Viewer by username.
	 * 
	 * @param username:
	 *            String
	 * @return Viewer
	 */
	Viewer findByUsername(String username);

	/**
	 * This method is used to find Viewer by phone.
	 * 
	 * @param phone:
	 *            String
	 * @return Viewer
	 */
	Viewer findByPhone(String phone);

	/**
	 * This method is used to find Viewer by email.
	 * 
	 * @param email:
	 *            String
	 * @return Viewer
	 */
	Viewer findByEmail(String email);

	/**
	 * This method is used to find Viewer by email or phone.
	 * 
	 * @param email:
	 *            String
	 * @param phone:
	 *            String
	 * @return List&lt;Viewer&gt;
	 */
	List<Viewer> findByEmailOrPhone(String email, String phone);

		/**
	 * This method is used to find Viewer by CHatName.
	 * 
	 * @param email:
	 *            String
	 * @return Viewer
	 */
	Viewer  findByChatName(String chatName);


	Viewer findByFacebookGmailId(String id);
}
