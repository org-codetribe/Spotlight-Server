package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventReview;
import com.yappyapps.spotlight.domain.Viewer;

/**
 * The IEventReviewRepository interface provides the CRUD operations on EventReview domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IEventReviewRepository extends CrudRepository<EventReview, Integer> {

	List<EventReview> findByEvent(Event event);

	EventReview findOneByEventAndViewer(Event event, Viewer viewer); 

	List<EventReview> findByViewer(Viewer viewer);

}
