package com.yappyapps.spotlight.service.impl;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The EmailNotificationService class is the implementation of
 * IEmailNotificationService
 * 
 * <h1>@Service</h1> denotes that it is a service class *
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class EmailNotificationService implements IEmailNotificationService {
	/**
	 * Logger for the class.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(EmailNotificationService.class);

	/**
	 * JavaMailSender dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	@Autowired
	public JavaMailSender emailSender;

	/**
	 * Email id from which mail will be sent
	 * <h1>@Value</h1> will enable the value read from properties file.
	 */
	@Value("${spring.mail.from}")
	private String mailFrom;

	/**
	 * Email id to which mail will be sent
	 * <h1>@Value</h1> will enable the value read from properties file.
	 */
	@Value("${spring.mail.to}")
	private String mailTo;

	/**
	 * Email id to which mail will be CCed
	 * <h1>@Value</h1> will enable the value read from properties file.
	 */
	@Value("${spring.mail.cc}")
	private String mailCC;

	/**
	 * Mail Subject
	 * <h1>@Value</h1> will enable the value read from properties file.
	 */
	@Value("${spring.mail.subject.resetpassword}")
	private String mailSubject;

	/**
	 * This method is used to send the email message.
	 * 
	 * @param message:
	 *            String
	 */
	@Async
	public void sendMimeMessage(String message) throws MessagingException, AddressException {

		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			mimeMessageHelper.setFrom(mailFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(mailTo, false));
			mimeMessageHelper.setCc(InternetAddress.parse(mailCC, false));
			mimeMessageHelper.setSubject(mailSubject);
			mimeMessageHelper.setText(message, true);
			mimeMessageHelper.setSentDate(new Date());
			emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			LOGGER.error(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
			throw new MessagingException(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
		}
	}

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
	 */
	@Async
	public void sendMimeMessage(String message, String mailTo, String mailCC, String subject)
			throws MessagingException, AddressException {

		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			mimeMessageHelper.setFrom(mailFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(mailTo, false));
			mimeMessageHelper.setCc(InternetAddress.parse(mailCC, false));
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(getEmailBody(message), true);
			mimeMessageHelper.setSentDate(new Date());
			emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			LOGGER.error(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
			throw new MessagingException(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
		}
	}

	/**
	 * This method is used to send the email message.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @param subject:
	 *            String
	 * 
	 */
	@Async
	public void sendMimeMessage(SpotlightUser spotlightUser, String subject)
			throws MessagingException, AddressException {

		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			mimeMessageHelper.setFrom(mailFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(spotlightUser.getEmail(), false));
			mimeMessageHelper.setCc(InternetAddress.parse(spotlightUser.getEmail(), false));
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(getEmailBody(spotlightUser), true);
			mimeMessageHelper.setSentDate(new Date());
			emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			LOGGER.error(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
			throw new MessagingException(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
		}
	}

	/**
	 * This method is used to send the email message.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @param subject:
	 *            String
	 * 
	 */
	@Async
	public void sendMimeMessage(Viewer viewer, String subject) throws MessagingException, AddressException {

		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			mimeMessageHelper.setFrom(mailFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(viewer.getEmail(), false));
			mimeMessageHelper.setCc(InternetAddress.parse(viewer.getEmail(), false));
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(getEmailBody(viewer), true);
			mimeMessageHelper.setSentDate(new Date());
			emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			LOGGER.error(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
			throw new MessagingException(IConstants.PARSE_MAIL_EXCEPTION + Utils.getStackTrace(e));
		}
	}

	/**
	 * This method is used to prepare the message body.
	 * 
	 * @param message:
	 *            String
	 * @return String: body
	 * 
	 */
	public static String getEmailBody(String message) {
		String body = "<div style='width:528px;min-height:40px;background-color:#888888;padding-top:5px;padding-right:10px;padding-bottom:5px;padding-left:10px'><a href='http://www.yappyapps.com/'><img width='50' height='50' border='0' style='margin-top:7px' src='http://imaginei.in/spotlight/images/logo.png' class='CToWUd'></a></div><div style='padding:10px;float:left;width:528px'><h1 style='color:#5c5c5c;float:left;width:528px;font-family:Arial,Helvetica,sans-serif;font-size:16px;font-weight:bold'>Dear User,</h1><div style='color:#666666;float:left;width:528px;margin-top:10px;margin-bottom:10px;line-height:20px'><b style='color:blue'>"
				+ message
				+ "</b> <br/> <br/><div style='color:#666666;float:left;width:528px;margin-bottom:10px;line-height:20px'><strong>Sincerely, <br>The Spotlight Team</strong></div><div style='color:#666666;float:left;width:528px;margin-bottom:10px;line-height:20px'>p.s. If you have any questions, please feel free to contact us at<a style='color:#26456e;text-decoration:none;font-weight:bold' href='#148eac976a826889_142b99c38e3880a5_'>&nbsp; support@yappyapps.com</a></div></div>";
		return body;
	}

	/**
	 * This method is used to prepare the message body.
	 * 
	 * @param spotlightUser:
	 *            SpotlightUser
	 * @return String: body
	 * 
	 */
	public static String getEmailBody(SpotlightUser spotlightUser) {
		String body = "<div style='width:528px;min-height:40px;background-color:#888888;padding-top:5px;padding-right:10px;padding-bottom:5px;padding-left:10px'><a href='http://www.yappyapps.com/'><img width='50' height='50' border='0' style='margin-top:7px' src='http://imaginei.in/spotlight/images/logo.png' class='CToWUd'></a></div><div style='padding:10px;float:left;width:528px'><h1 style='color:#5c5c5c;float:left;width:528px;font-family:Arial,Helvetica,sans-serif;font-size:16px;font-weight:bold'>Dear User,</h1>Your Spotlight account as "
				+ spotlightUser.getUserType()
				+ " is now active! <br> <br> Your account details are:<div style='color:#666666;float:left;width:528px;margin-top:10px;margin-bottom:10px;line-height:20px'><b style='color:blue'>"
				+ "Name : " + spotlightUser.getName() + "<br>" + "EmailId : " + spotlightUser.getEmail() + "<br>"
				+ "Password : " + spotlightUser.getPassword() + "<br>"
				+ "</b> <br/> <br/><div style='color:#666666;float:left;width:528px;margin-bottom:10px;line-height:20px'><strong>Sincerely, <br>The Spotlight Team</strong></div><div style='color:#666666;float:left;width:528px;margin-bottom:10px;line-height:20px'>p.s. If you have any questions, please feel free to contact us at<a style='color:#26456e;text-decoration:none;font-weight:bold' href='#148eac976a826889_142b99c38e3880a5_'>&nbsp; support@yappyapps.com</a></div></div>";
		return body;
	}

	/**
	 * This method is used to prepare the message body.
	 * 
	 * @param viewer:
	 *            Viewer
	 * @return String: body
	 * 
	 */
	public static String getEmailBody(Viewer viewer) {
		String body = "<div style='width:528px;min-height:40px;background-color:#888888;padding-top:5px;padding-right:10px;padding-bottom:5px;padding-left:10px'><a href='http://www.yappyapps.com/'><img width='50' height='50' border='0' style='margin-top:7px' src='http://imaginei.in/spotlight/images/logo.png' class='CToWUd'></a></div><div style='padding:10px;float:left;width:528px'><h1 style='color:#5c5c5c;float:left;width:528px;font-family:Arial,Helvetica,sans-serif;font-size:16px;font-weight:bold'>Dear User,</h1>Your password has been reset. <br> <br> Your account details are:<div style='color:#666666;float:left;width:528px;margin-top:10px;margin-bottom:10px;line-height:20px'><b style='color:blue'>"
				// + "Name : " + spotlightUser.getName() + "<br>"
				+ "EmailId : " + viewer.getEmail() + "<br>" + "Password : " + viewer.getPassword() + "<br>"
				+ "</b> <br/> <br/><div style='color:#666666;float:left;width:528px;margin-bottom:10px;line-height:20px'><strong>Sincerely, <br>The Spotlight Team</strong></div><div style='color:#666666;float:left;width:528px;margin-bottom:10px;line-height:20px'>p.s. If you have any questions, please feel free to contact us at<a style='color:#26456e;text-decoration:none;font-weight:bold' href='#148eac976a826889_142b99c38e3880a5_'>&nbsp; support@yappyapps.com</a></div></div>";
		return body;
	}
}
