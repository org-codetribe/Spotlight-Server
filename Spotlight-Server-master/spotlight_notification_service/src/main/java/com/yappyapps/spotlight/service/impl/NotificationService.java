package com.yappyapps.spotlight.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.ViewerEvent;
import com.yappyapps.spotlight.repository.IEventRepository;
import com.yappyapps.spotlight.repository.IViewerEventRepository;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.service.INotificationService;
import com.yappyapps.spotlight.util.IConstants;

/**
 * The ViewerService class is the implementation of IViewerService
 * 
 * <h1>@Service</h1> denotes that it is a service class
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class NotificationService implements INotificationService {
	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	/**
	 * IEventRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IEventRepository eventRepository;

	/**
	 * IViewerEventRepository dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IViewerEventRepository viewerEventRepository;

	/**
	 * IEmailNotificationService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private IEmailNotificationService emailNotificationService;

	/**
	 * PushNotificationsService dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	private PushNotificationsService pushNotificationsService;

	/**
	 * BROADCASTER_TOPIC
	 * <h1>@Value</h1> will enable the value read from properties file.
	 */
	@Value("${firebase.broadcaster.topic}")
	private String BROADCASTER_TOPIC ;

	/**
	 * VIEWER_TOPIC
	 * <h1>@Value</h1> will enable the value read from properties file.
	 */
	@Value("${firebase.viewer.topic}")
	private String VIEWER_TOPIC ;

	@Scheduled(fixedRate = 300000)
	public void checkEventTime() {
		
		Timestamp current = new Timestamp(System.currentTimeMillis());
	   	Calendar cal = Calendar.getInstance();
	   	cal.setTimeInMillis(current.getTime());
	   	cal.add(Calendar.SECOND, 300);
	   	Timestamp later = new Timestamp(cal.getTime().getTime());
		List<Event> eventList = eventRepository.findByStatusAndEventUtcDatetimeBetween("Active", current, later);
		LOGGER.info("current :: " + current);
		LOGGER.info("later :: " + later);
		LOGGER.info("Event List Size :: " + eventList.size());
		
		for(Event event : eventList) {
			sendEmailNotificationToBraodcaster(event);
			sendPushNotificationToBraodcaster(event);
			sendPushNotificationToViewer(event);
			
			List<ViewerEvent> viewerEventList = viewerEventRepository.findByEvent(event);
			
			for(ViewerEvent viewerEvent : viewerEventList) {
				sendEmailNotificationToViewer(viewerEvent);
			}
		}
		
		
	}
	
	@Override
	public void sendEmailNotificationToBraodcaster(Event event) {
		String broadcasterEmail = event.getBroadcasterInfo().getSpotlightUser().getEmail();
		
		if(broadcasterEmail != null) {
			try {
				emailNotificationService.sendMimeMessage(
						"Get Ready to start the Live Stream! <BR> <BR> Event is going to start at " + event.getEventUtcDatetime() + "!",
						broadcasterEmail, broadcasterEmail,
						IConstants.UPCOMING_EVENT);
			} catch (MessagingException e) {
				LOGGER.error("Error while sending email to Broadcaster :: " + broadcasterEmail);
			}

		}

		LOGGER.info("Email Sent to Broadcaster " + event.getBroadcasterInfo().getDisplayName() + " at " + dateTimeFormatter.format(LocalDateTime.now()));
		
	}

	@Override
	public void sendPushNotificationToBraodcaster(Event event) {

		String topic = BROADCASTER_TOPIC + "-" + event.getUniqueName();
		try {
			
			JSONObject body = new JSONObject();
			body.put("to", "/topics/" + topic);
			body.put("priority", "high");
	 
			JSONObject notification = new JSONObject();
			notification.put("title", IConstants.UPCOMING_EVENT);
			notification.put("body", "Get Ready to start the Live Stream! <BR> <BR> Event is going to start at " + event.getEventUtcDatetime() + "!");
			
			JSONObject data = new JSONObject();
			data.put("Key-1", "JSA Data 1");
			data.put("Key-2", "JSA Data 2");
	 
			body.put("notification", notification);
			body.put("data", data);
			
			HttpEntity<String> request = new HttpEntity<>(body.toString());
			 
			CompletableFuture<String> pushNotification = pushNotificationsService.send(request);
			CompletableFuture.allOf(pushNotification).join();
	 
			String firebaseResponse = pushNotification.get();
			LOGGER.info("firebaseResponse :::: " + firebaseResponse);
		} catch (InterruptedException e) {
			LOGGER.error("Error while sending push notifications to topic :: " + topic);
		} catch (ExecutionException e) {
			LOGGER.error("Error while sending push notifications to topic :: " + topic);
		}

		
		LOGGER.info("Broadcaster Push Message Sent to Topic " + topic + " at " + dateTimeFormatter.format(LocalDateTime.now()));
		
	}

	@Override
	public void sendEmailNotificationToViewer(ViewerEvent viewerEvent){
		
		String viewerEmail = viewerEvent.getViewer().getEmail();
		if(viewerEmail != null) {
			try {
				emailNotificationService.sendMimeMessage(
						"Get Ready to watch the Live Stream! <BR> <BR> Event is going to start at " + viewerEvent.getEvent().getEventUtcDatetime() + "!",
						viewerEmail, viewerEmail,
						IConstants.UPCOMING_EVENT);
			} catch (MessagingException e) {
				LOGGER.error("Error while sending email to Broadcaster :: " + viewerEmail);
			}

		}
		LOGGER.info("Email Sent to Viewer " + viewerEvent.getViewer().getChatName() + " at " + dateTimeFormatter.format(LocalDateTime.now()));
		
	}

	@Override
	public void sendPushNotificationToViewer(Event event){
		
		String topic = VIEWER_TOPIC + "-" + event.getUniqueName();
		try {
			
			JSONObject body = new JSONObject();
			body.put("to", "/topics/" + topic);
			body.put("priority", "high");
	 
			JSONObject notification = new JSONObject();
			notification.put("title", IConstants.UPCOMING_EVENT);
			notification.put("body", "Get Ready to start the Live Stream! <BR> <BR> Event is going to start at " + event.getEventUtcDatetime() + "!");
			
			JSONObject data = new JSONObject();
			data.put("Key-1", "JSA Data 1");
			data.put("Key-2", "JSA Data 2");
	 
			body.put("notification", notification);
			body.put("data", data);
			
			HttpEntity<String> request = new HttpEntity<>(body.toString());
			 
			CompletableFuture<String> pushNotification = pushNotificationsService.send(request);
			CompletableFuture.allOf(pushNotification).join();
	 
			String firebaseResponse = pushNotification.get();
			LOGGER.info("firebaseResponse :::: " + firebaseResponse);
		} catch (InterruptedException e) {
			LOGGER.error("Error while sending push notifications to topic :: " + topic);
		} catch (ExecutionException e) {
			LOGGER.error("Error while sending push notifications to topic :: " + topic);
		}

		
		LOGGER.info("Viewer Push Message Sent to Topic " + topic + " at " + dateTimeFormatter.format(LocalDateTime.now()));
		
		
	}


}
