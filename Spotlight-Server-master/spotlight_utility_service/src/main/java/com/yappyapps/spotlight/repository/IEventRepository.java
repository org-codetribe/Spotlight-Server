package com.yappyapps.spotlight.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.EventType;

/**
 * The IEventRepository interface provides the CRUD operations on Event domain
 *
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IEventRepository extends CrudRepository<Event, Integer> {

    /**
     * This method is used to find all the Events by status.
     *
     * @param status: String
     * @return List&lt;Event&gt;
     */
    List<Event> findAllByStatus(String status);

    /**
     * This method is used to find all the Events by status with paging.
     *
     * @param status:   String
     * @param pageable: Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findAllByStatus(String status, Pageable pageable);

    /**
     * This method is used to find all the Events with paging.
     *
     * @param pageable: Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findAll(Pageable pageable);

    /**
     * This method is used to find all the Events by BroadcasterInfo.
     *
     * @param broadcasterInfo: BroadcasterInfo
     * @return List&lt;Event&gt;
     */
    List<Event> findByBroadcasterInfo(BroadcasterInfo broadcasterInfo);

    List<Event> findByBroadcasterInfoAndEventUtcDatetimeGreaterThanEqual(BroadcasterInfo broadcasterInfo, Timestamp current);
    @Query(value = "from event e where e.event_UTC_Datetime >= (sysdate() + event.event_duration + 1/24)",nativeQuery = true)
    List<Event> findAllByEventUtcDatetimeGreaterThanEqual();


    @Query(value = "select * from event where LOWER(display_name) LIKE lower(concat('%', :name,'%')) and event_UTC_Datetime >= (sysdate() + event.event_duration + 1/24)",nativeQuery = true)
    List<Event> findAllByEventUtcDatetimeGreaterThanEqual(String name);
    @Query(value = "SELECT info.id,COUNT(event.broadcaster_info_id) AS event_count FROM spotlight.event INNER JOIN spotlight.broadcaster_info info where event.broadcaster_info_id = info.id and event_UTC_Datetime >= (sysdate()+event.event_duration + 1/24) group by event.broadcaster_info_id order by event_count desc",nativeQuery = true)
    List<Object[]> countByEventUtcDatetimeGreaterThanEqual();

    /**
     * This method is used to find all the Events by BroadcasterInfo with paging.
     *
     * @param broadcasterInfo: BroadcasterInfo
     * @param pageable:        Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findByBroadcasterInfo(BroadcasterInfo broadcasterInfo, Pageable pageable);

    /**
     * This method is used to find all Events by EventType.
     *
     * @param eventType: EventType
     * @return List&lt;Event&gt;
     */
    List<Event> findByEventType(EventType eventType);

    List<Event> findByEventTypeAndEventUtcDatetimeIsGreaterThanEqualOrderByEventUtcDatetimeAsc(EventType eventType, Timestamp current);


    /**
     * This method is used to find all Events by EventType with paging.
     *
     * @param eventType: EventType
     * @param pageable:  Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findByEventType(EventType eventType, Pageable pageable);

    /**
     * This method is used to find all Event by isTrending.
     *
     * @param isTrending: Boolean
     * @param timestamp:  Timestamp
     * @return List&lt;Event&gt;
     */
    List<Event> findByIsTrendingAndEventUtcDatetimeGreaterThan(Boolean isTrending, Timestamp timestamp);

    /**
     * This method is used to find all Event by isTrending with paging.
     *
     * @param isTrending: Boolean
     * @param timestamp:  Timestamp
     * @param pageable:   Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findByIsTrendingAndEventUtcDatetimeGreaterThan(Boolean isTrending, Timestamp timestamp, Pageable pageable);

    /**
     * This method is used to find all Event by status and isTrending.
     *
     * @param status:     String
     * @param isTrending: Boolean
     * @param timestamp:  Timestamp
     * @return List&lt;Event&gt;
     */
    List<Event> findByStatusAndIsTrendingAndEventUtcDatetimeGreaterThan(String status, Boolean isTrending, Timestamp timestamp);

    /**
     * This method is used to find all Event by status and isTrending with
     * paging.
     *
     * @param status:     String
     * @param isTrending: Boolean
     * @param timestamp:  Timestamp
     * @param pageable:   Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findByStatusAndIsTrendingAndEventUtcDatetimeGreaterThan(String status, Boolean isTrending, Timestamp timestamp, Pageable pageable);


    /**
     * This method is used to find all Event by  Broadcaster and status and event date greater than.
     *
     * @param broadcasterInfo: BroadcasterInfo
     * @param status:          String
     * @param timestamp:       Timestamp
     * @return List&lt;Event&gt;
     */
    List<Event> findByBroadcasterInfoAndStatusAndEventUtcDatetimeGreaterThan(BroadcasterInfo broadcasterInfo,
                                                                             String status, Timestamp timestamp);

    /**
     * This method is used to find all Event by  Broadcaster and and status.
     *
     * @param broadcasterInfo: BroadcasterInfo
     * @param status:          String
     * @return List&lt;Event&gt;
     */
    List<Event> findByBroadcasterInfoAndStatus(BroadcasterInfo broadcasterInfo, String status);

    /**
     * This method is used to find all Event by  Broadcaster and status with paging.
     *
     * @param broadcasterInfo: BroadcasterInfo
     * @param status:          String
     * @param pageable:        Pageable
     * @return Page&lt;Event&gt;
     */
    Page<Event> findByBroadcasterInfoAndStatus(BroadcasterInfo broadcasterInfo, String status,
                                               Pageable pageable);

    /**
     * This method is used to find all Event by name.
     *
     * @param eventName:       String
     * @param broadcasterInfo: BroadcasterInfo
     * @return List&lt;Event&gt;
     */
    List<Event> findByBroadcasterInfoAndDisplayNameContaining(BroadcasterInfo broadcasterInfo, String eventName);

    /**
     * This method is used to find all Event by status and isTrending.
     *
     * @param status:     String
     * @param startTime: Boolean
     * @param endTime:  Timestamp
     * @return List&lt;Event&gt;
     */
    List<Event> findByStatusAndEventUtcDatetimeBetween(String status, Timestamp startTime, Timestamp endTime);

}
