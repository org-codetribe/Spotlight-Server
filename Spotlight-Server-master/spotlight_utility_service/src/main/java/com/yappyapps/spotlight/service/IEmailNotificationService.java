package com.yappyapps.spotlight.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;

/**
 * The IEmailNotificationService interface declares all the operations to send
 * Email
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public interface IEmailNotificationService {
	/**
	 * This method is used to send the email message.
	 * 
	 * @param message:
	 *            String
	 * 
	 * @throws MessagingException
	 *             MessagingException
	 * @throws AddressException
	 *             AddressException
	 */
	public void sendMimeMessage(String message) throws MessagingException, AddressException;

	/**
	 * This method is used to send the email message.
	 * 
	 * @param message:
	 *            String
	 * @param mailTo:
	 *            String
	 * @param mailCC:
	 *            String
	 * @param subject:
	 *            String
	 * 
	 * @throws MessagingException
	 *             MessagingException
	 * @throws AddressException
	 *             AddressException
	 * 
	 */
	public void sendMimeMessage(String message, String mailTo, String mailCC, String subject)
			throws MessagingException, AddressException;

	/**
	 * This method is used to send the email message.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @param subject:
	 *            String
	 * 
	 * @throws MessagingException
	 *             MessagingException
	 * @throws AddressException
	 *             AddressException
	 * 
	 */
	public void sendMimeMessage(SpotlightUser spotlightUser, String subject)
			throws MessagingException, AddressException;

	/**
	 * This method is used to send the email message.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @param subject:
	 *            String
	 * 
	 * @throws MessagingException
	 *             MessagingException
	 * @throws AddressException
	 *             AddressException
	 */
	public void sendMimeMessage(Viewer viewer, String subject) throws MessagingException, AddressException;

}
