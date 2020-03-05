package com.yappyapps.spotlight.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.ViewerSession;

/**
 * The IViewerSessionRepository interface provides the CRUD operations on ViewerSession domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IViewerSessionRepository extends CrudRepository<ViewerSession, Integer> {

	ViewerSession findByViewer(Viewer viewer);

	@Transactional
	void deleteByAuthToken(String authToken);

	@Transactional
	void deleteByViewer(Viewer viewer);

	ViewerSession findByAuthToken(String authToken);
	Viewer findByEmail(String email);



}
