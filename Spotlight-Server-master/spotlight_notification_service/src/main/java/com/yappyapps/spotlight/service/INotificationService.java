package com.yappyapps.spotlight.service;

import com.yappyapps.spotlight.domain.Event;
import com.yappyapps.spotlight.domain.ViewerEvent;

/**
 * The INotificationService interface declares   
 * all the operations to schedule
 * 
 * @author  Naveen Goswami
 * @version 1.0
 * @since   2018-07-14 
 */

public interface INotificationService {

	public void sendEmailNotificationToBraodcaster(Event event);
	
	public void sendPushNotificationToBraodcaster(Event event);
	
	public void sendEmailNotificationToViewer(ViewerEvent viewerEvent);

	public void sendPushNotificationToViewer(Event event);
}
