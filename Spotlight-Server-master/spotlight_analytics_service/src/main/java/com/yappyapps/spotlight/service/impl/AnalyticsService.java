package com.yappyapps.spotlight.service.impl;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IBroadcasterInfoRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.service.IAnalyticsService;
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
public class AnalyticsService implements IAnalyticsService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsService.class);

	/**
	 * Utils dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private Utils utils;

	/**
	 * ISpotlightUserRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private ISpotlightUserRepository spotlightUserRepository;

	/**
	 * IBroadcasterInfoRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IBroadcasterInfoRepository broadcasterInfoRepository;


	@PersistenceContext
	private EntityManager em;

	@Override
	public String getTop5Events(String startDate, String endDate, String country, Integer spotlightUserId) throws ParseException, ResourceNotFoundException {
		String result = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = null;
		try {
			formattedStartDate = formatter.parse(startDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Query query = null;
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		if(spotlightUserId == null) {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query =	em.createQuery("SELECT e.displayName, p.event.id, ROUND(Sum(p.amount), 2) as revenue FROM Event e"
						+ "		INNER JOIN PaymentTransaction p ON e.id = p.event.id "
						+ "		where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate "
						+ " 	and e.country = :country group by p.event.id order by Sum(p.amount) desc");
		
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("country", country);
			} else {
				query =	em.createQuery("SELECT e.displayName, p.event.id, ROUND(Sum(p.amount), 2) as revenue FROM Event e"
						+ "		INNER JOIN PaymentTransaction p ON e.id = p.event.id "
						+ "		where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate "
						+ " 	group by p.event.id order by Sum(p.amount) desc");
		
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			}
		} else {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query =	em.createQuery("SELECT e.displayName, p.event.id, ROUND(Sum(p.amount), 2) as revenue FROM Event e"
							+ "		INNER JOIN PaymentTransaction p ON e.id = p.event.id "
							+ "		where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate "
							+ " and e.country = :country and e.broadcasterInfo.id = :broadcasterId group by p.event.id order by Sum(p.amount) desc"
							+ "");
			
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate",  formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("country", country);
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
			} else {
				query =	em.createQuery("SELECT e.displayName, p.event.id, ROUND(Sum(p.amount), 2) as revenue FROM Event e"
						+ "		INNER JOIN PaymentTransaction p ON e.id = p.event.id "
						+ "		where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate "
						+ " 	and e.broadcasterInfo.id = :broadcasterId group by p.event.id order by Sum(p.amount) desc"
						+ "");
		
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate",  formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
			}
		}
		query.setMaxResults(5);
		List<Object[]> list = query.getResultList();
		LOGGER.info("Size :: " + list.size());
		Map<String, List<Map<String, Object>>> finalMap = new HashedMap();
		List<Map<String, Object>> listMap = new ArrayList<>();
		for (Object[] obj : list) {
			Map<String, Object> map = new HashedMap();
			map.put("eventName", obj[0]);
			map.put("eventId", obj[1]);
			map.put("revenue", obj[2]);
			listMap.add(map);
		}
		finalMap.put("data", listMap);
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(finalMap);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject jObj = new JSONObject(json);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getTop5BroadCasters(String startDate, String endDate, String country) throws ParseException {
		// TODO Auto-generated method stub
		String result = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = null;
		try {
			formattedStartDate = formatter.parse(startDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		Query query =null;
		if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
			query =	em.createQuery("SELECT bi.displayName, ROUND(Sum(p.amount), 2) as revenue, bi.id FROM Event e"
					+ "		INNER JOIN PaymentTransaction p ON e.id = p.event.id "
					+ "		LEFT JOIN BroadcasterInfo bi ON bi.id = e.broadcasterInfo.id "
					+ "		where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate "
					+ " and e.country = :country group by bi.id order by Sum(p.amount) desc"
					+ "");
			query.setParameter("startDate", formattedStartDate);
			query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			query.setParameter("country", country);
		} else {
			query =	em.createQuery("SELECT bi.displayName, ROUND(Sum(p.amount), 2) as revenue, bi.id FROM Event e"
					+ "		INNER JOIN PaymentTransaction p ON e.id = p.event.id "
					+ "		LEFT JOIN BroadcasterInfo bi ON bi.id = e.broadcasterInfo.id "
					+ "		where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate "
					+ " 	group by bi.id order by Sum(p.amount) desc"
					+ "");
			query.setParameter("startDate", formattedStartDate);
			query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			
		}
		query.setMaxResults(5);
		List<Object[]> list = query.getResultList();
		LOGGER.info("Size :: " + list.size());
		Map<String, List<Map<String, Object>>> finalMap = new HashedMap();
		List<Map<String, Object>> listMap = new ArrayList<>();

		for (Object[] objects : list) {
			Map<String, Object> map = new HashedMap();
			map.put("broadcasterName", objects[0]);
			map.put("revenue", objects[1]);
			map.put("broadcasterId", objects[2]);
			listMap.add(map);
		}

		finalMap.put("data", listMap);
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(finalMap);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject jObj = new JSONObject(json);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getEventsCount(String startDate, String endDate, String country, Integer spotlightUserId)
			throws ParseException, ResourceNotFoundException {
		// TODO Auto-generated method stub
		String result = null;
		Gson gsonObj = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		Query query = null;
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		if (null != spotlightUserId && spotlightUserId != 0) {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"SELECT count(*) FROM Event e where e.eventUtcDatetime > :startDate and e.eventUtcDatetime < :endDate and e.broadcasterInfo.id = :broadcasterId  and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"SELECT count(*) FROM Event e where e.eventUtcDatetime > :startDate and e.eventUtcDatetime < :endDate and e.broadcasterInfo.id = :broadcasterId");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
			}
		} else {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"SELECT count(*) FROM Event e where e.eventUtcDatetime > :startDate and e.eventUtcDatetime < :endDate  and e.country = :country ");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"SELECT count(*) FROM Event e where e.eventUtcDatetime > :startDate and e.eventUtcDatetime < :endDate ");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			}
		}
		long count = (long) query.getSingleResult();
		map.put("events", count);
		String response = gsonObj.toJson(map);
		JSONObject jObj = new JSONObject(response);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getBroadcastersCount(String startDate, String endDate, String country)
			throws ParseException {
		String result = "";
		// TODO Auto-generated method stub
		Gson gsonObj = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		Query query = null;
		if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
			query = em.createQuery(
					"SELECT count(*) FROM BroadcasterInfo b inner join SpotlightUser su ON b.spotlightUser.id = su.id where su.createdOn > :startDate and su.createdOn < :endDate and su.country = :country");
			query.setParameter("startDate", formattedStartDate);
			query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			query.setParameter("country", country);
		} else {
			query = em.createQuery(
					"SELECT count(*) FROM BroadcasterInfo b inner join SpotlightUser su ON b.spotlightUser.id = su.id where su.createdOn > :startDate and su.createdOn < :endDate ");
			query.setParameter("startDate", formattedStartDate);
			query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
		}
		long count = (long) query.getSingleResult();
		map.put("broadcasters", count);
		String response = gsonObj.toJson(map);
		JSONObject jObj = new JSONObject(response);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getViewersCount(String startDate, String endDate, String country)
			throws ParseException {
		String result = "";
		// TODO Auto-generated method stub
		Gson gsonObj = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		Query query = null;
		if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
			query = em.createQuery(
					"SELECT count(*) FROM Viewer where createdOn > :startDate and createdOn < :endDate");
			query.setParameter("startDate", formattedStartDate);
			query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
//			query.setParameter("country", country);
		} else {
			query = em.createQuery(
					"SELECT count(*) FROM Viewer where createdOn > :startDate and createdOn < :endDate");
			query.setParameter("startDate", formattedStartDate);
			query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
		}
		long count = (long) query.getSingleResult();
		map.put("viewers", count);
		String response = gsonObj.toJson(map);
		JSONObject jObj = new JSONObject(response);
		result = utils.constructSucessJSON(jObj);

		return result;
	}


	@Override
	public String getTotalRevenue(String startDate, String endDate, String country, Integer spotlightUserId)
			throws ParseException, ResourceNotFoundException {
		String result = "";
		// TODO Auto-generated method stub
		Gson gsonObj = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		Query query = null;
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}

		if (null != spotlightUserId && spotlightUserId != 0) {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"select ROUND(sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e On p.event.id = e.id where e.broadcasterInfo.id = :broadcasterId and p.paymentDatetime > :startDate and p.paymentDatetime < :endDate and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"select ROUND(sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e On p.event.id = e.id where e.broadcasterInfo.id = :broadcasterId and p.paymentDatetime > :startDate and p.paymentDatetime < :endDate");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
			}
		} else {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"select ROUND(sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e On p.event.id = e.id where p.paymentDatetime > :startDate and p.paymentDatetime < :endDate and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"select ROUND(sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e On p.event.id = e.id where p.paymentDatetime > :startDate and p.paymentDatetime < :endDate");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			}
		}


		Double count = (Double) query.getSingleResult();
		map.put("revenue", count);
		String response = gsonObj.toJson(map);
		JSONObject jObj = new JSONObject(response);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getTotalLikes(String startDate, String endDate, String country, Integer spotlightUserId)
			throws ParseException, ResourceNotFoundException {
		String result = "";
		// TODO Auto-generated method stub
		Gson gsonObj = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		Query query = null;
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		if (null != spotlightUserId && spotlightUserId != 0) {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id  where er.isLike = true and er.createdOn > :startDate and er.createdOn < :endDate and e.broadcasterInfo.id = :broadcasterId and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id  where er.isLike = true and er.createdOn > :startDate and er.createdOn < :endDate and e.broadcasterInfo.id = :broadcasterId");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
			}
		} else {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id where er.isLike = true and er.createdOn > :startDate and er.createdOn < :endDate and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id where er.isLike = true and er.createdOn > :startDate and er.createdOn < :endDate ");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			}
		}
		long count = (long) query.getSingleResult();
		map.put("likes", count);
		String response = gsonObj.toJson(map);
		JSONObject jObj = new JSONObject(response);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getTotalDislikes(String startDate, String endDate, String country, Integer spotlightUserId)
			throws ParseException, ResourceNotFoundException {
		String result = "";
		// TODO Auto-generated method stub
		Gson gsonObj = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		Query query = null;
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		if (null != spotlightUserId && spotlightUserId != 0) {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id  where er.isLike = false and er.createdOn > :startDate and er.createdOn < :endDate and e.broadcasterInfo.id = :broadcasterId and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id  where er.isLike = false and er.createdOn > :startDate and er.createdOn < :endDate and e.broadcasterInfo.id = :broadcasterId");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
			}
		} else {
			if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id where er.isLike = false and er.createdOn > :startDate and er.createdOn < :endDate and e.country = :country");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				query.setParameter("country", country);
			} else {
				query = em.createQuery(
						"select count(*)  from EventReview er INNER JOIN Event e ON er.event.id = e.id where er.isLike = false and er.createdOn > :startDate and er.createdOn < :endDate ");
				query.setParameter("startDate", formattedStartDate);
				query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
			}
		}
		long count = (long) query.getSingleResult();
		map.put("dislikes", count);
		String response = gsonObj.toJson(map);
		JSONObject jObj = new JSONObject(response);
		result = utils.constructSucessJSON(jObj);

		return result;
	}

	@Override
	public String getGraphData(String startDate, String endDate, String country, Integer spotlightUserId) throws ParseException, ResourceNotFoundException {
		// TODO Auto-generated method stub
		Gson gsonObj = new Gson();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date formattedStartDate = formatter.parse(startDate);
		Query query = null;
		LOGGER.info("startDate :: " + startDate);
		LOGGER.info("endDate :: " + endDate);
		float daysBetween = getDaysBetweenDates(startDate, endDate);
		LOGGER.info("daysBetween :: " + daysBetween);
		Map<String, List<Map<String, Object>>> finalMap = new HashedMap();
		if(country == null || country.trim().equals("")|| country.trim().equals("undefined")) {
			country = "";
		}
		if (daysBetween <= 12) {
			// daily data
			LOGGER.info("in 12 :: ");
			if (null != spotlightUserId && spotlightUserId != 0) {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select DATE(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId   and e.country = :country group by DATE(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select DATE(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId group by DATE(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				}
			} else {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select DATE(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.country = :country group by DATE(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select DATE(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate group by DATE(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				}
			}
			List<Object[]> list = query.getResultList();
			List<Map<String, Object>> listMap = new ArrayList<>();
			LOGGER.info("in 12 :: list.size() :: " + list.size());
			for (Object[] objects : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", objects[0]);
				map.put("count", objects[1]);
				listMap.add(map);
			}
			LOGGER.info("end 12 :: ");
			finalMap.put("produceYearlyData", listMap);

		} else if (daysBetween > 12 && daysBetween <= 31) {
			// weekly data
			LOGGER.info("in 12 - 31 :: ");
			if (null != spotlightUserId && spotlightUserId != 0) {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId and e.country = :country  group by FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)) ORDER BY FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7))");					
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId group by FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)) ORDER BY FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7))");					
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				}
			} else {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.country = :country group by FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)) ORDER BY FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7))");					
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate group by FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7)) ORDER BY FROM_DAYS(TO_DAYS(p.paymentDatetime) -MOD(TO_DAYS(p.paymentDatetime) -1, 7))");					
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				}
			}
				
			List<Object[]> list = query.getResultList();
			List<Map<String, Object>> listMap = new ArrayList<>();
			for (Object[] objects : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", objects[0]);
				map.put("count", objects[1]);
				listMap.add(map);
			}
			LOGGER.info("end 12 - 31 :: ");
			finalMap.put("produceYearlyData", listMap);

		} else if (daysBetween > 31 && daysBetween <= 365) {
			// monthly data
			LOGGER.info("in 31 - 365 :: ");
			if (null != spotlightUserId && spotlightUserId != 0) {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select MONTH(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId   and e.country = :country group by MONTH(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select MONTH(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId group by MONTH(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				}
			} else {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select MONTH(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.country = :country group by MONTH(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select MONTH(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate group by MONTH(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				}
			}
			List<Object[]> list = query.getResultList();
			List<Map<String, Object>> listMap = new ArrayList<>();
			for (Object[] objects : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				Integer month = (Integer) objects[0];
				map.put("name", new DateFormatSymbols().getMonths()[month - 1]);
				map.put("count", objects[1]);
				listMap.add(map);
			}
			LOGGER.info("end 31 - 365 :: ");
			finalMap.put("produceYearlyData", listMap);

		} else {
			// yearly data
			LOGGER.info("in > 365 :: ");
			if (null != spotlightUserId && spotlightUserId != 0) {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select YEAR(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId   and e.country = :country group by YEAR(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select YEAR(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.broadcasterInfo.id = :broadcasterId  group by YEAR(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("broadcasterId", getBroadcasterInfoIdFromSpotlightUserId(spotlightUserId));
				}
			} else {
				if(country != null && !country.equalsIgnoreCase("") && !country.equalsIgnoreCase("All")) {
					query = em.createQuery(
							"select YEAR(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate and e.country = :country group by YEAR(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
					query.setParameter("country", country);
				} else {
					query = em.createQuery(
							"select YEAR(p.paymentDatetime), ROUND(Sum(p.amount), 2) as revenue from PaymentTransaction p INNER JOIN Event e ON e.id = p.event.id where p.paymentDatetime < :endDate and p.paymentDatetime > :startDate group by YEAR(p.paymentDatetime)");
					query.setParameter("startDate", formattedStartDate);
					query.setParameter("endDate", formatter.parse(utils.getEndDate(endDate)));
				}
			}
			List<Object[]> list = query.getResultList();
			List<Map<String, Object>> listMap = new ArrayList<>();
			for (Object[] objects : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", objects[0]);
				map.put("count", objects[1]);
				listMap.add(map);
			}
			LOGGER.info("end > 365 :: ");
			finalMap.put("produceYearlyData", listMap);

		}

		String response = gsonObj.toJson(finalMap);
		return response;
	}

	private float getDaysBetweenDates(String startDate, String endDate) {
		// TODO Auto-generated method stub
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		float daysBetween = 0;
		try {
			Date dateBefore = myFormat.parse(startDate);
			Date dateAfter = myFormat.parse(endDate);
			long difference = dateAfter.getTime() - dateBefore.getTime();
			daysBetween = (difference / (1000 * 60 * 60 * 24));
			/*
			 * You can also convert the milliseconds to days using this method float
			 * daysBetween = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
			 */
			LOGGER.info("Number of Days between dates: " + daysBetween);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daysBetween;
	}
	
	private Integer getBroadcasterInfoIdFromSpotlightUserId(Integer spotlightUserId) throws ResourceNotFoundException {
		
		Optional<SpotlightUser> spotlightUser = spotlightUserRepository.findById(spotlightUserId);
		
		if(!spotlightUser.isPresent())
			throw new ResourceNotFoundException("Spotlight User do not exist.");
		
		BroadcasterInfo broadcasterInfo = broadcasterInfoRepository.findBySpotlightUser(spotlightUser.get());
		
		if(broadcasterInfo == null)
			throw new ResourceNotFoundException("Broadcaster do not exist.");
			
		
		return broadcasterInfo.getId();
		
	}
	
}
