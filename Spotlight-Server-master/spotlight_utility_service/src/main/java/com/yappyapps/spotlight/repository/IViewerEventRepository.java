package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.ViewerEvent;

/**
 * The IViewerRepository interface provides the CRUD operations on Viewer Event domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IViewerEventRepository extends JpaRepository<ViewerEvent, Integer> {

	/**
	 * This method is used to retrieve the count by Event.
	 * 
	 * @param event:
	 *            Event
	 * @return Long
	 */
	Long countByEvent(Event event);

	/**
	 * This method is used to retrieve the count by Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @return Long
	 */
	Long countByViewer(Viewer viewer);

	/**
	 * This method is used to find all the events by Viewer.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @return List&lt;ViewerEvent;&gt;
	 */
	List<ViewerEvent> findByViewer(Viewer viewer);

	/**
	 * This method is used to find all the events by Viewer with paging and orderBy.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;ViewerEvent;&gt;
	 */
	Page<ViewerEvent> findByViewer(Viewer viewer, Pageable pageable);

	/**
	 * This method is used to find all the Viewers by Event.
	 * 
	 * @param event:
	 *            Event
	 * @return List&lt;ViewerEvent;&gt;
	 */
	List<ViewerEvent> findByEvent(Event event);

	/**
	 * This method is used to check if event has been purchased by Viewer.
	 * 
	 * @param event:
	 *            Event
	 * @param viewer:
	 *            Viewer
	 * @return Page&lt;ViewerEvent;&gt;
	 */
	Boolean existsByEventAndViewer(Event event, Viewer viewer);

	/**
	 * This method is used to check if event has been purchased by Viewer.
	 * 
	 * @param event:
	 *            Event
	 * @param viewer:
	 *            Viewer
	 * @return Page&lt;ViewerEvent;&gt;
	 */
	List<ViewerEvent> findByEventAndViewer(Event event, Viewer viewer);



}
