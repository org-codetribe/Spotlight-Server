package com.yappyapps.spotlight.util;

/**
* The IConstants interface declares   
* all the constants used in the application 
* 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
public interface IConstants {

	boolean ACL_CHECK_ON = true;
	
	String DEFAULT_USER_TYPE = "ADMIN"; 

	String PARSE_MAIL_EXCEPTION = "Exception in parsing Mime Mail Message";
	
	String EMAIL_SUBJECT_PASSWORD_RESET = "Spotlight Account Password Reset";
	
	String EMAIL_SUBJECT_ACCOUNT_CREDENTIALS = "Spotlight Account Credentials";
	
	String EMAIL_SUBJECT_PASSWORD_CHANGE = "Spotlight Account Password Change";
	
	String UPCOMING_EVENT = "Spotlight Upcoming Event"; 
	
	String TOTAL_RECORDS = "totalRecords";
	
	String CURRENT_PAGE = "currentPage";
 
	String CURRENT_PAGE_RECORDS = "currentPageRecords";
	
	String DEFAULT_ORDERBY = "id";

	String DEFAULT_ORDERBY_DIRECTION = "ASC";

	String DEFAULT_STATUS = "ACTIVE";

	String INTERNAL_SERVER_ERROR = "Internal Server Error. Please try again";
	
	String ALREADY_EXIST_MESSAGE = "Resource with same details already exists.";
	
	String ACCOUNT_DISABLED_MESSAGE = "Your account is disabled. Please contact the Administrator.";
	
	String BAD_CREDENTIALS_MESSAGE = "Credentials are not valid.";
	
	String CLIENT_NOT_AUTHORIZED_MESSAGE = "Client is not Authorized.";
	
	String RESOURCE_NOT_FOUND_MESSAGE = "Resource(s) do not exist.";
	
	String ACCESS_DENIED_MESSAGE = "You are not allowed to perform this operation.";
	
	String OLD_PASSWORD_DONOT_MATCH = "Old Password do not match.";

	String SUCCESS = "success";

	String RESPONSE = "response";

	String ROLE = "role";
	
	String ROLES = "roles";

	String AUDITLOG = "auditLog";
	
	String AUDITLOGS = "auditLogs";

	String BROADCASTER = "broadcaster";

	String BROADCASTERS = "broadcasters";
	
	String EVENT = "event";
	
	String EVENTS = "events";
	
	String CATEGORIES = "categories";
	
	String EVENTREVIEW = "eventReview";
	
	String EVENTREVIEWS = "eventReviews";

	String USER = "user";
	
	String USERS = "users";
	
	String EVENTTYPE = "eventType";
	
	String EVENTTYPES = "eventTypes";

	String LIVESTREAMCONFIG = "liveStreamConfig";

	String LIVESTREAMCONFIGS = "liveStreamConfigs";

	String PRICINGRULE = "pricingRule";
	
	String PRICINGRULES = "pricingRules";

	String GENRE = "genre";
	
	String GENRES = "genres";

	String CHILDREN = "child";

	String AUTH = "auth";

	String VIEWER = "viewer";
	
	String VIEWERS = "viewers";

	String COUPON = "coupon";
	
	String COUPONS = "coupons";
	
	String BRAINTREE_TOKEN = "token";

	String TRANSACTION = "transaction";
}