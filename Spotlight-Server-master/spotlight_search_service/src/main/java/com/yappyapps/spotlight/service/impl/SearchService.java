package com.yappyapps.spotlight.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.helper.BroadcasterInfoHelper;
import com.yappyapps.spotlight.domain.helper.EventHelper;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.ISearchService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The SearchService class is the implementation of ISearchService
 * 
 * <h1>@Service</h1> denotes that it is a service class
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class SearchService implements ISearchService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

//	/**
//	 * IGenreRepository dependency will be automatically injected.
//	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
//	 */
//	@Autowired
//	private IGenreRepository genreRepository;
//
//	/**
//	 * ISpotlightCommissionRepository dependency will be automatically injected.
//	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
//	 */
//	@Autowired
//	private ISpotlightCommissionRepository spotlightCommissionRepository;
//
//	/**
//	 * IAccessRoleRepository dependency will be automatically injected.
//	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
//	 */
//	@Autowired
//	private IAccessRoleRepository accessRoleRepository;
//
//	/**
//	 * PasswordEncoder dependency will be automatically injected.
//	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
//	 */
//	@Autowired
//	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private BroadcasterInfoHelper broadcasterInfoHelper;

	@Autowired
	private EventHelper eventHelper;

//	@Autowired
//	private GenreHelper genreHelper;

//	@Autowired
//	private SpotlightUserHelper spotlightUserHelper;
	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

    @Autowired
    private final EntityManager centityManager;


    @Autowired
    public SearchService(EntityManager entityManager) {
        super();
        this.centityManager = entityManager.getEntityManagerFactory().createEntityManager();;
    }


    public void initializeHibernateSearch() {

        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
	 * This method is used to search based on searchTerm.
	 * 
	 * @param searchTerm: String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	@Override
    @Transactional
    public String fuzzySearch(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception{
		String result = "";

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(BroadcasterInfo.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "biography", "shortDesc",  "genre.name", "spotlightUser.address1", "spotlightUser.address2", "spotlightUser.name", "spotlightUser.phone", "spotlightUser.city", "spotlightUser.country", "spotlightUser.state", "spotlightUser.zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        
        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, BroadcasterInfo.class);

        // execute search
        List<BroadcasterInfo> broadcasterInfoList = null;
        try {
            broadcasterInfoList = jpaQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

        FullTextEntityManager fullTextEventEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder eventqb = fullTextEventEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
        Query eventLuceneQuery = eventqb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "description",  "eventType.name", "address1", "address2", "city", "country", "state", "zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        javax.persistence.Query jpaEventQuery = fullTextEventEntityManager.createFullTextQuery(eventLuceneQuery, Event.class);

        // execute search

        List<Event> eventList = null;
        try {
        	eventList = jpaEventQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }
        
        if((broadcasterInfoList == null || broadcasterInfoList.size() <= 0 ) && (eventList == null || eventList.size() <= 0)) {
        	throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        
        JSONObject jObj = new JSONObject();
		jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
		jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null));
		
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	/**
	 * This method is used to search by searchTerm with paging.
	 * 
	 * @param searchTerm: String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	@Override
	public String fuzzySearch(String searchTerm, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
//		String result = null;
//		long totalCount = 0;
//		GenreHelper genreHelper = new GenreHelper(genreRepository);
//		List<Genre> genreList = new ArrayList<Genre>();
//		int pageNum = offset / limit;
//		try {
//			Direction directionObj = (direction != null ? Direction.valueOf(direction)
//					: Direction.valueOf(IConstants.DEFAULT_ORDERBY_DIRECTION));
//			orderBy = (orderBy != null ? orderBy : IConstants.DEFAULT_ORDERBY);
//			Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
//			Page<Genre> genres = genreRepository.findAll(pageableRequest);
//			totalCount = genres.getTotalElements();
//			List<Genre> genreEntities = genres.getContent();
//			for (Genre genreEntity : genreEntities) {
//				Genre genreDto = new Genre();
//				BeanUtils.copyProperties(genreEntity, genreDto);
//				genreList.add(genreDto);
//			}
//		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
//			throw new Exception(sqlException.getMessage());
//		} catch (HibernateException | JpaSystemException sqlException) {
//			throw new Exception(sqlException.getMessage());
//		}
//
//		if (genreList.size() <= 0) {
//			throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
//		}
//
//		JSONObject jObj = new JSONObject();
//		jObj.put(IConstants.GENRES, genreHelper.buildResponseObject(genreList));
//		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
//		jObj.put(IConstants.CURRENT_PAGE, pageNum);
//		jObj.put(IConstants.CURRENT_PAGE_RECORDS, genreList.size());
//
//		result = utils.constructSucessJSON(jObj);
//
//		return result;
		String result = "";
		long totalCount = 0;
//		int pageNum = offset / limit;
//		Sort sort = new Sort(new SortField("displayName", Type.valueOf("ASC")));
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(BroadcasterInfo.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "biography", "shortDesc",  "genre.name", "spotlightUser.address1", "spotlightUser.address2", "spotlightUser.name", "spotlightUser.phone", "spotlightUser.city", "spotlightUser.country", "spotlightUser.state", "spotlightUser.zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, BroadcasterInfo.class);
        List<BroadcasterInfo> broadcasterInfoListWithoutPaging = null;
        try {
        	broadcasterInfoListWithoutPaging = jpaQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }
        // execute search
        jpaQuery.setFirstResult(offset);
        jpaQuery.setMaxResults(limit);
//        jpaQuery.setSort(sort);
        
//        Sort sort = SortUtils.getLuceneSortWithDefaults(searchParameters.getSort(), QueuedTaskHolderSort.ID);
        List<BroadcasterInfo> broadcasterInfoList = null;
        try {
            broadcasterInfoList = jpaQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

		Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
		orderBy = (orderBy != null ? orderBy : "id");
		int pageNum = offset / limit;
//        Pageable page = PageRequest.of(pageNum, limit, directionObj, orderBy);
//        Page<BroadcasterInfo> broadcasterInfoPageList= new PageImpl<BroadcasterInfo>(broadcasterInfoList ,page,broadcasterInfoList.size());
        totalCount = broadcasterInfoListWithoutPaging.size();
        
        FullTextEntityManager fullTextEventEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder eventqb = fullTextEventEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
        Query eventLuceneQuery = eventqb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "description",  "eventType.name", "address1", "address2", "city", "country", "state", "zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        FullTextQuery jpaEventQuery = fullTextEventEntityManager.createFullTextQuery(eventLuceneQuery, Event.class);

        // execute search
        jpaEventQuery.setFirstResult(offset);
        jpaEventQuery.setMaxResults(limit);
//        jpaQuery.setSort(sort);
        List<Event> eventList = null;
        try {
        	eventList = jpaEventQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

        if((broadcasterInfoList == null || broadcasterInfoList.size() <= 0 ) && (eventList == null || eventList.size() <= 0)) {
        	throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
		jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
		jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());

		result = utils.constructSucessJSON(jObj);

		return result;
	}

    /**
	 * This method is used to search based on searchTerm.
	 * 
	 * @param searchTerm: String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	@Override
    @Transactional
    public String fuzzySearchBroadcasters(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception{
		String result = "";

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(BroadcasterInfo.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "biography", "shortDesc",  "genre.name", "spotlightUser.address1", "spotlightUser.address2", "spotlightUser.name", "spotlightUser.phone", "spotlightUser.city", "spotlightUser.country", "spotlightUser.state", "spotlightUser.zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        
        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, BroadcasterInfo.class);

        // execute search
        List<BroadcasterInfo> broadcasterInfoList = null;
        try {
            broadcasterInfoList = jpaQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

        if(broadcasterInfoList == null || broadcasterInfoList.size() <= 0 ) {
        	throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        
        JSONObject jObj = new JSONObject();
		jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
		
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	/**
	 * This method is used to search by searchTerm with paging.
	 * 
	 * @param searchTerm: String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	@Override
	public String fuzzySearchBroadcasters(String searchTerm, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = "";
		int pageNum = offset / limit;
		long totalCount = 0;
//		int pageNum = offset / limit;
//		Sort sort = new Sort(new SortField("displayName", Type.valueOf("ASC")));
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(BroadcasterInfo.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "biography", "shortDesc",  "genre.name", "spotlightUser.address1", "spotlightUser.address2", "spotlightUser.name", "spotlightUser.phone", "spotlightUser.city", "spotlightUser.country", "spotlightUser.state", "spotlightUser.zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, BroadcasterInfo.class);

        List<BroadcasterInfo> broadcasterInfoListWithoutPaging = null;
        try {
        	broadcasterInfoListWithoutPaging = jpaQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }
        totalCount = broadcasterInfoListWithoutPaging.size();
        // execute search
        jpaQuery.setFirstResult(offset);
        jpaQuery.setMaxResults(limit);
//        jpaQuery.setSort(sort);
        List<BroadcasterInfo> broadcasterInfoList = null;
        try {
            broadcasterInfoList = jpaQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

        if(broadcasterInfoList == null || broadcasterInfoList.size() <= 0 ) {
        	throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        
        JSONObject jObj = new JSONObject();
		jObj.put(IConstants.BROADCASTERS, broadcasterInfoHelper.buildResponseObject(broadcasterInfoList, null));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, broadcasterInfoList.size());
		
		result = utils.constructSucessJSON(jObj);

		return result;
	}

    /**
	 * This method is used to search based on searchTerm.
	 * 
	 * @param searchTerm: String
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	@Override
    @Transactional
    public String fuzzySearchEvents(String searchTerm) throws ResourceNotFoundException, BusinessException, Exception{
		String result = "";

        FullTextEntityManager fullTextEventEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder eventqb = fullTextEventEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
        Query eventLuceneQuery = eventqb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "description",  "eventType.name", "address1", "address2", "city", "country", "state", "zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        javax.persistence.Query jpaEventQuery = fullTextEventEntityManager.createFullTextQuery(eventLuceneQuery, Event.class);

        // execute search

        List<Event> eventList = null;
        try {
        	eventList = jpaEventQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

        if(eventList == null || eventList.size() <= 0 ) {
        	throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        
        JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null));
		
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	/**
	 * This method is used to search by searchTerm with paging.
	 * 
	 * @param searchTerm: String
	 * @param limit:
	 *            Integer
	 * @param offset:
	 *            Integer
	 * @param direction:
	 *            String
	 * @param orderBy:
	 *            String
	 * 
	 * @return String: Response
	 * 
	 * @throws ResourceNotFoundException
	 *             ResourceNotFoundException
	 * @throws BusinessException
	 *             BusinessException
	 * @throws Exception
	 *             Exception
	 */
	@Override
	public String fuzzySearchEvents(String searchTerm, Integer limit, Integer offset, String direction, String orderBy)
			throws ResourceNotFoundException, BusinessException, Exception {
		String result = "";
		int pageNum = offset / limit;
		long totalCount = 0;
//		Sort sort = new Sort(new SortField("displayName", Type.valueOf("ASC")));
        FullTextEntityManager fullTextEventEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder eventqb = fullTextEventEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
        Query eventLuceneQuery = eventqb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("displayName", "description",  "eventType.name", "address1", "address2", "city", "country", "state", "zip")
                .matching(searchTerm).createQuery();
        LOGGER.info("Searching ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        FullTextQuery jpaEventQuery = fullTextEventEntityManager.createFullTextQuery(eventLuceneQuery, Event.class);

        List<Event> eventListWithoutPaging = null;
        try {
        	eventListWithoutPaging = jpaEventQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }
        totalCount = eventListWithoutPaging.size();

        // execute search
        jpaEventQuery.setFirstResult(offset);
        jpaEventQuery.setMaxResults(limit);
//        jpaQuery.setSort(sort);
        List<Event> eventList = null;
        try {
        	eventList = jpaEventQuery.getResultList();
            LOGGER.info("Searched ::::::::::::::::::::::::::::::::::::: " + searchTerm);
        } catch (NoResultException nre) {
           LOGGER.error("error");
           nre.printStackTrace();
        }

        if(eventList == null || eventList.size() <= 0 ) {
        	throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        
        JSONObject jObj = new JSONObject();
		jObj.put(IConstants.EVENTS, eventHelper.buildResponseObject(eventList, null));
		jObj.put(IConstants.TOTAL_RECORDS, totalCount);
		jObj.put(IConstants.CURRENT_PAGE, pageNum);
		jObj.put(IConstants.CURRENT_PAGE_RECORDS, eventList.size());
		
		result = utils.constructSucessJSON(jObj);

		return result;
	}
}
