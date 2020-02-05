package com.yappyapps.spotlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.EventType;

/**
 * The IEventTypeRepository interface provides the CRUD operations on EventType
 * domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IEventTypeRepository extends CrudRepository<EventType, Integer> {

	/**
	 * This method is used to find the EventType with paging.
	 * 
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;EventType&gt;
	 */
	Page<EventType> findAll(Pageable pageable);

	/**
	 * This method is used to find the EventType based on status.
	 * 
	 * @param status:
	 *            String
	 * @return List&lt;EventType&gt;
	 */
	List<EventType> findAllByStatus(String status);

	/**
	 * This method is used to find the EventType based on status.
	 * 
	 * @param status:
	 *            String
	 * @param pageable:
	 *            Pageable
	 * @return Page&lt;EventType&gt;
	 */
	Page<EventType> findAllByStatus(String status, Pageable pageable);

	/**
	 * This method is used to find the EventType based on name.
	 * 
	 * @param name:
	 *            name to find
	 * @return EventType
	 */
	EventType findByName(String name);

	/**
	 * This method is used to find all EventType by Parent EventType.
	 * 
	 * @param eventType:
	 *            EventType
	 * @return List&lt;EventType&gt;
	 */
	List<EventType> findByEventType(EventType eventType);

	/**
	 * This method is used to find all EventType orderBy name.
	 * 
	 * @return List&lt;EventType&gt;
	 */
	List<EventType> findAllByOrderByName();

	/**
	 * This method is used to find all EventType by status orderBy name.
	 * 
	 * @param status:
	 *            String
	 * @return List&lt;EventType&gt;
	 */
	List<EventType> findAllByStatusOrderByName(String status);

	List<EventType> findByIsCategory(Boolean isCategory);

}
