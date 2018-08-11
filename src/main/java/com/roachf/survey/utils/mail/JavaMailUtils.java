package com.roachf.survey.utils.mail;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件工具类
 *
 * @author roach
 */

@Slf4j
public class JavaMailUtils {
	
	/**
	 * 发送文本邮件
	 * @param javaMail
	 * @return
	 */
	public static boolean sendTextMail(JavaMail javaMail){
		return sendMail(javaMail, false);
	}
	
	/**
	 * 发送HTML邮件
	 * @param javaMail
	 * @return
	 */
	public static boolean sendHtmlMail(JavaMail javaMail){
		return sendMail(javaMail, true);
	}
	
	private static boolean sendMail(final JavaMail javaMail, boolean isHtml){
		
		boolean flag = true;
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", javaMail.getHost());
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(javaMail.getUsername(), javaMail.getPassword());
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(javaMail.getUsername()));
			
			// 接收邮件邮箱： 只可发送单个用户
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(javaMail.getToMail()));
			
			// 抄送邮箱: 可抄送多个用户 
			if (javaMail.getCcMails() != null && javaMail.getCcMails().length > 0) {
				StringBuilder cc = new StringBuilder(javaMail.getCcMails()[0]);
				for (int i = 1; i < javaMail.getCcMails().length; i++) {
					cc.append(",").append(javaMail.getCcMails()[i]);
				}
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc.toString()));
			}
			// 密送邮箱：可密送多个用户 
			if (javaMail.getBccMails() != null && javaMail.getBccMails().length > 0) {
				StringBuilder bcc = new StringBuilder(javaMail.getBccMails()[0]);
				for (int i = 1; i < javaMail.getBccMails().length; i++) {
					bcc.append(",").append(javaMail.getBccMails()[i]);
				}
				message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc.toString()));
			}
			
			// 设置邮箱主题 
			message.setSubject(javaMail.getSubject());
			
			Multipart multipart = new MimeMultipart();
			
			/* 第一部分： 设置邮件body正文 */
			BodyPart bodyPart = new MimeBodyPart();
			if(isHtml){
				bodyPart.setContent(javaMail.getContent(), "text/html;charset=UTF-8");
			}else{
				bodyPart.setText(javaMail.getContent());
			}
			multipart.addBodyPart(bodyPart);
			
			/* 第二部分： 设置邮件附件 */
			if(javaMail.getAttachments() != null && javaMail.getAttachments().length > 0){
				for (int i = 0; i < javaMail.getAttachments().length; i++) {
					bodyPart = new MimeBodyPart();
			        String filename = javaMail.getAttachments()[i];
			        DataSource source = new FileDataSource(filename);
			        bodyPart.setDataHandler(new DataHandler(source));
			        bodyPart.setFileName(source.getName());
			        multipart.addBodyPart(bodyPart);
				}
			}
			
			message.setContent(multipart);
			Transport.send(message);

		} catch (MessagingException e) {
			flag = false;
			log.error("消息异常：", e);
		}
		return flag;
	}
}
