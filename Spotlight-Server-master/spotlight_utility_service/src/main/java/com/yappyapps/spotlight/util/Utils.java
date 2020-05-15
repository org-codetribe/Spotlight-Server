package com.yappyapps.spotlight.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.yappyapps.spotlight.domain.JwtViewer;
import com.yappyapps.spotlight.domain.SpotlightUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import com.yappyapps.spotlight.exception.InvalidParameterException;

/**
 * The Utils class is utility class
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public class Utils {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	/**
	 * This static method is used to get the stacktrace from exception.
	 * 
	 * @param exception
	 *            : Exception
	 * @return String : exceptionMessage
	 * 
	 */
	public static String getStackTrace(Exception exception) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionMessage = sw.toString();
		pw.close();
		return exceptionMessage;
	}

	/**
	 * This method is used to check the obj for Empty and Null and construct the
	 * Exception message.
	 *
	 * @param obj
	 *            object which has to be checked for Empty or Null
	 * @param name
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isEmptyOrNull(Object obj, String name) throws InvalidParameterException {
		if (obj == null) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' cannot be null or empty.");
		}
		if (obj instanceof String) {
			if (((String) obj).trim().equals("")) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' cannot be null or empty.");
			}
		}
		if (obj instanceof ArrayList) {
			if (((ArrayList) obj).size() == 0) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' cannot be null or empty.");
			}
		}
	}

	/**
	 * This method is used to check the valid email format and construct the
	 * Exception message.
	 *
	 * @param email
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isEmailValid(String email) throws InvalidParameterException {
		if (email == null) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'Email' cannot be null or empty.");
		}
		if (email instanceof String) {
			if (((String) email).trim().equals("")) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter 'Email' cannot be null or empty.");
			}
		}
		boolean valid = EmailValidator.getInstance().isValid(email);
		if (!valid) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'Email' is not valid.");
		}
	}

	/**
	 * This method is used to check the validity of User Type and construct the
	 * Exception message.
	 *
	 * @param userType
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isUserTypeValid(String userType) throws InvalidParameterException {
		if (userType == null) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'User Type' cannot be null or empty.");
		}
		if (userType instanceof String) {
			if (((String) userType).trim().equals("")) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter 'User Type' cannot be null or empty.");
			}
			if (userType.equalsIgnoreCase("SUPERADMIN")) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter 'User Type' value cannot be 'SUPERADMIN'.");
			}

			if (!(userType.equalsIgnoreCase("ADMIN") || userType.equalsIgnoreCase("BROADCASTER")
					|| userType.equalsIgnoreCase("SALES") || userType.equalsIgnoreCase("MANAGEMENTCOMPANY"))) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter 'User Type' values can only be from [ADMIN, BROADCASTER, SALES, MANAGEMENTCOMPANY].");
			}
		}

	}

	/**
	 * This method is used to check the validity of status and construct the
	 * Exception message.
	 *
	 * @param status
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isStatusValid(String status) throws InvalidParameterException {

		if (status != null && !(status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("INACTIVE"))) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'Status' values can only be from [ACTIVE, INACTIVE].");
		}
	}

	/**
	 * This method is used to check the validity of Access Type and construct the
	 * Exception message.
	 *
	 * @param accessType
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isAccessTypeValid(String accessType) throws InvalidParameterException {

		if (accessType != null && !(accessType.equalsIgnoreCase("READ") || accessType.equalsIgnoreCase("WRITE"))) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'Access' values can only be from [READ, WRITE].");
		}
	}

	/**
	 * This method is used to check the validity of order by direction and construct
	 * the Exception message.
	 *
	 * @param direction
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isOrderByDirectionValid(String direction) throws InvalidParameterException {

		if (direction != null && !(direction.equalsIgnoreCase("ASC") || direction.equalsIgnoreCase("DESC"))) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'direction' values can only be from [ASC, DESC].");
		}
	}

	/**
	 * This method is used to check the validity of order by property and construct
	 * the Exception message.
	 *
	 * @param property
	 *            the parameter to be checked.
	 * @param classObj
	 *            the Class to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isOrderByPropertyValid(String property, Class classObj) throws InvalidParameterException {
		Field[] fields = classObj.getDeclaredFields();
		LOGGER.info("size ::::::::::::::::::::::: " + fields.length);
		boolean propertyMatched = false;
		if (property != null) {
			for (Field field : fields) {
				if (property.equalsIgnoreCase(field.getName())) {
					propertyMatched = true;
					break;
				}
			}
			if (!propertyMatched)
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter 'property' values cannot be " + property
								+ ".");
		}
	}

	/**
	 * This method is used to check the validity of AccessRole Type and construct
	 * the Exception message.
	 *
	 * @param accessRoleType
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isAccessRoleTypeValid(String accessRoleType) throws InvalidParameterException {

		if (accessRoleType != null
				&& !(accessRoleType.equalsIgnoreCase("Global") || accessRoleType.equalsIgnoreCase("Module"))) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'type' values can only be from [Global, Module].");
		}
	}

	/**
	 * This method is used to check the input parameters for Integer.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isInteger(Object obj, String name) throws InvalidParameterException {
		try {
			Integer.parseInt(obj.toString());
		} catch (NumberFormatException ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' Should be Integer.");
		}
	}

	/**
	 * This method is used to check the input parameters for boolean.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isBoolean(Object obj, String name) throws InvalidParameterException {
		try {
			Boolean.parseBoolean(obj.toString());
		} catch (Exception ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' Should be Boolean.");
		}
	}

	/**
	 * This method is used to check the input parameters for JSON Object.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isJSONObject(Object obj, String name) throws InvalidParameterException {
		try {
			new JSONObject(obj.toString());
		} catch (JSONException ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' Should be JSON Object.");
		}
	}

	/**
	 * This method is used to check the input parameters for JSON Object.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isJSONArray(Object obj, String name) throws InvalidParameterException {
		if (obj != null) {
			try {
				new JSONArray(obj.toString());
			} catch (JSONException ex) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' Should be JSON Array.");
			}
		}
	}

	/**
	 * This method is used to check the body for JSON Object.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isBodyJSONObject(Object obj) throws InvalidParameterException {
		try {
			new JSONObject(obj.toString());
		} catch (JSONException ex) {
			throw new InvalidParameterException("Request is invalid. Body should be JSON Object.");
		}
	}

	/**
	 * This method is used to create the unique id.
	 *
	 * @param input
	 *            the parameter which will be used to create the unique id.
	 * @return String Unique Id
	 */
	public static String uniqueIdGenerator(String input) {
		String charset = input + new Date().getTime();
		Integer length = 32;
		String uniqueId = RandomStringUtils.random(length, charset.toCharArray());
		return uniqueId;
	}

	/**
	 * This method is used to create the unique id.
	 *
	 * @param input
	 *            the parameter which will be used to create the unique id.
	 * @param length
	 *            length of the unique id.
	 * @return String Unique Id
	 */
	public static String uniqueIdGenerator(String input, int length) {
		String charset = input + new Date().getTime();
		String uniqueId = RandomStringUtils.random(length, charset.toCharArray());
		return uniqueId;
	}

	/**
	 * This method is used to create the random string.
	 *
	 * @param len
	 *            the parameter length.
	 * @return String Random String
	 */
	public static String generateRandomString(int len) {
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Small_chars = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		// String symbols = "!@#$%^&*_=+-/.?<>)";

		String values = Capital_chars + Small_chars + numbers;

		SecureRandom random = new SecureRandom();

		String randomString = "";

		for (int i = 0; i < len; i++) {
			int index = random.nextInt(values.length());
			randomString += values.charAt(index);

		}
		return randomString;
	}

	/**
	 * This method is used to create the random password.
	 *
	 * @param len
	 *            the parameter length.
	 * @return String Random Password
	 */
	public static String generateRandomPassword(int len) {
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Small_chars = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		// String symbols = "!@#$%^&*_=+-/.?<>)";

		String values = Capital_chars + Small_chars + numbers;

		SecureRandom random = new SecureRandom();

		String password = "";

		for (int i = 0; i < len; i++) {
			int index = random.nextInt(values.length());
			password += values.charAt(index);

		}
		return password;
	}

	/**
	 * This method is used to check the Object for empty or not.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isAvailableObjectEmpty(Object obj, String name) throws InvalidParameterException {
		if (obj == null) {
			return;
		}
		if (obj instanceof String) {
			if (((String) obj).trim().equals("")) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' cannot be null or empty.");
			}
		}
		if (obj instanceof ArrayList) {
			if (((ArrayList) obj).size() == 0) {
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' cannot be null or empty.");
			}
		}
	}

	/**
	 * This method is used to check the input parameters for Integer.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isIntegerGreaterThanZero(Object obj, String name) throws InvalidParameterException {
		try {
			int value = Integer.parseInt(obj.toString());
			if (value < 1)
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' should not be empty.");
		} catch (NumberFormatException ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' should be Integer.");
		}
	}

	/**
	 * This method is used to check the input parameters for Big Decimal.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isBigDecimalGreaterThanZero(Object obj, String name) throws InvalidParameterException {
		try {
			BigDecimal value = new BigDecimal(obj.toString());
			if (value.doubleValue() <= 0)
				throw new InvalidParameterException(
						"The Request parameters are invalid. The parameter '" + name + "' should not be empty.");
		} catch (NumberFormatException ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' should be BigDecimal.");
		}
	}

	/**
	 * This method is used to check the input parameters for Float.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isFloat(Object obj, String name) throws InvalidParameterException {
		try {
			Float.parseFloat(obj.toString());
		} catch (NumberFormatException ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' should be Float.");
		}
	}

	/**
	 * This method is used to check the input parameters for Valid Percentage.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isValidPercentage(Object obj, String name) throws InvalidParameterException {
		try {
			Float.parseFloat(obj.toString());
		} catch (NumberFormatException ex) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' should be Float.");
		}
		
		if(Float.parseFloat(obj.toString()) < 0 || Float.parseFloat(obj.toString()) > 100) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter '" + name + "' value should be between 0 and 100.");
		}
	}

	/**
	 * This method is used to check the input parameters for valid date.
	 *
	 * @param obj
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isValidDate(Object obj, String name) throws InvalidParameterException {
		// try
		// {
		// new java.sql.Timestamp(Integer.parseInt(obj.toString()));
		// } catch (NumberFormatException ex) {
		// throw new InvalidParameterException(
		// "The Request parameters are invalid. The parameter '"+ name +"' should be
		// Time in milliseconds.");
		// }
	}

	/**
	 * This method is used to check the input parameters minimum length validity.
	 *
	 * @param value
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @param length
	 *            the parameter length
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isMinLengthValid(String value, String name, int length) throws InvalidParameterException {
		if (value != null && value.trim().length() < length) {
			throw new InvalidParameterException("The Request parameters are invalid. The parameter '" + name
					+ "' length should not be less than " + length + ".");
		}

	}

	/**
	 * This method is used to check the input parameters length validity.
	 *
	 * @param value
	 *            object which has to be checked
	 * @param name
	 *            the parameter name
	 * @param length
	 *            the parameter length
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isLengthValid(String value, String name, int length) throws InvalidParameterException {
		if (value != null && value.length() > length) {
			throw new InvalidParameterException("The Request parameters are invalid. The parameter '" + name
					+ "' length should be less than " + length + ".");
		}

	}

	/**
	 * This method is used to construct the success response as JSON.
	 * 
	 * @param jobj:
	 *            JSONObject
	 * @return String: Response
	 * 
	 * @throws JSONException
	 *             JSONException
	 * 
	 */
	public String constructSucessJSON(JSONObject jobj) throws JSONException {
		jobj.put(IConstants.SUCCESS, true);
		JSONObject responseJObj = new JSONObject();
		responseJObj.put(IConstants.RESPONSE, jobj);
		LOGGER.debug(responseJObj.toString());
		return responseJObj.toString();

	}


	/**
	 * This method is used to build the response object from UserDetails.
	 * 
	 * @param jwtTokenUtil:
	 *            JwtTokenUtil
	 * @param userDetails:
	 *            UserDetails
	 * @return JSONObject: Response
	 * 
	 * @throws JSONException
	 *             JSONException
	 * 
	 */
	public JSONObject buildResponseObject(JwtTokenUtil jwtTokenUtil, UserDetails userDetails, SpotlightUser isBroadCasterExist) throws JSONException {
		
		final String token = jwtTokenUtil.generateToken(userDetails);
		final String username = jwtTokenUtil.getUsernameFromToken(token);
		final Date issuedAt = jwtTokenUtil.getIssuedAtDateFromToken(token);
		final Date expiresAt = jwtTokenUtil.getExpirationDateFromToken(token);

		JSONObject authObj = new JSONObject();
		authObj.put("token", token);
		if(username.startsWith("_S")){
			authObj.put("userLoginType", "BROADCASTER");
		}else{
			authObj.put("userLoginType", "VIEWER");
		}
		authObj.put("username", username);
		authObj.put("issuedAt", issuedAt.getTime());
		authObj.put("expiresAt", expiresAt.getTime());
		authObj.put("tokenType", "Bearer");
		if(isBroadCasterExist != null){
			authObj.put("isBroadCasterExist", true );
			authObj.put("brodcasterId",isBroadCasterExist.getId());

		}else{
			authObj.put("isBroadCasterExist",false);
			authObj.put("brodcasterId",isBroadCasterExist);
		}
		authObj.put("id",((JwtViewer) userDetails).getId());
	
		
		return authObj; 
		
	}
	
	
	/**
	 * This method is used to check the status.
	 * 
	 * @param status:
	 *            String
	 * @return boolean: Response
	 * 
	 * 
	 */
	public boolean isActive(String status) {
		boolean activeFlag = false;
		if(status != null && status.equalsIgnoreCase("Active")) {
			activeFlag = true;
		}
		return activeFlag;	

	}
	
	public Timestamp convertUTCToOtherTimeZone(Timestamp date, String timeZone) {
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LOGGER.info(date.toString());
		LocalDateTime localDateTime = LocalDateTime.parse(date.toString(), formatter);
		
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		ZonedDateTime utcDateTime = localDateTime.atZone(utcTimeZone.toZoneId());
		LOGGER.info("Date (UTC) : " + formatter.format(utcDateTime));
        
		TimeZone convertedTimeZone = TimeZone.getTimeZone(timeZone);
		
		ZonedDateTime convertedDateTime = utcDateTime.withZoneSameInstant(convertedTimeZone.toZoneId());
		LOGGER.info("Converted Date : " + formatter.format(convertedDateTime));
		
		date = Timestamp.valueOf(formatter.format(convertedDateTime));
		
		LOGGER.info("Returned Date : " + formatter.format(convertedDateTime));
		
		return date;
	}

	public Timestamp convertOtherTimeZoneToUTC(Timestamp date, String timeZone) {
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LOGGER.info(date.toString());
		LocalDateTime localDateTime = LocalDateTime.parse(date.toString(), formatter);
		
		TimeZone convertedTimeZone = TimeZone.getTimeZone(timeZone);
		ZonedDateTime convertedDateTime = localDateTime.atZone(convertedTimeZone.toZoneId());
		LOGGER.info("Converted Date : " + formatter.format(convertedDateTime));
        
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		
		ZonedDateTime utcDateTime = convertedDateTime.withZoneSameInstant(utcTimeZone.toZoneId());
		LOGGER.info("Date (UTC) : " + formatter.format(utcDateTime));
		
		date = Timestamp.valueOf(formatter.format(utcDateTime));
		
		LOGGER.info("Returned Date : " + formatter.format(utcDateTime));
		
		return date;
	}
	
	

	/**
	 * This method is used to check the validity of coupon type and construct the
	 * Exception message.
	 *
	 * @param type
	 *            the parameter to be checked.
	 * @throws InvalidParameterException
	 *             InvalidParameterException
	 */
	public void isCouponTypeValid(String type) throws InvalidParameterException {

		if (type != null && !(type.equalsIgnoreCase("Single") || type.equalsIgnoreCase("Multi"))) {
			throw new InvalidParameterException(
					"The Request parameters are invalid. The parameter 'type' values can only be from [Single, Multi].");
		}
	}


	public String getEndDate(String endDate)
			throws ParseException {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		final Date date = format.parse(endDate);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, +1);
		return format.format(calendar.getTime());
	}


	public static boolean isTimeStampValid(String inputString)
	{
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		try{
			format.parse(inputString);
			return true;
		}
		catch(ParseException e)
		{
			return false;
		}
	}


}
