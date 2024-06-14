package com.yangzhou.utils;

import java.util.Properties;

import com.yangzhou.pojo.Email;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


public class EmailUtil {
    /**
     * 发送邮件
     *
     * @param email   发件人信息(发件人邮箱,发件人授权码)及邮件服务器信息(邮件服务器域名,身份验证开关)
     * @param to      收件人邮箱
     * @param title   邮件标题
     * @param content 邮件正文
     * @return
     */
    public static boolean sendMail(Email email, String to, String title, String content) {
        MimeMessage message = null;
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", email.getHost());
            properties.put("mail.smtp.auth", email.isAuth());
            properties.put("mail.user", email.getUser());
            properties.put("mail.password", email.getCode());

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email.getUser(), email.getCode());
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(properties, authenticator);
            // 创建邮件消息
            message = new MimeMessage(mailSession);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //如果邮件创建失败,直接返回
        if (message == null) {
            return false;
        }

        try {
            // 设置发件人
            InternetAddress form = new InternetAddress(email.getUser());
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
