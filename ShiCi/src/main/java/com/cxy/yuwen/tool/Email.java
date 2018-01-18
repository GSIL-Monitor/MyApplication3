package com.cxy.yuwen.tool;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



/**
 * Created by cxy on 2017/8/5.
 */

public class Email {

    public  static boolean postEmail(String text) throws GeneralSecurityException {
      ///  System.out.println(message);
       boolean isSuccess=false;

        // 收件人电子邮箱
        String to = "17621503621@1.com";

        // 发件人电子邮箱
        String from = "1746569077@qq.com";

        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.qq.com";  //QQ 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");

        // 关于QQ邮箱，还要设置SSL加密，加上以下代码即可
      //  MailSSLSocketFactory sf = new MailSSLSocketFactory();
     //   sf.setTrustAllHosts(true);
    //    properties.put("mail.smtp.ssl.enable", "true");
      //  properties.put("mail.smtp.ssl.socketFactory", sf);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("1746569077@qq.com", "kvauiljlgzbxbfif"); //发件人邮件用户名、授权码
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject("This is the Subject Line!");

            // 设置消息体
            message.setText(text);

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
            isSuccess=true;
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
          return  isSuccess;
    }

    public  static void main(String [] args){
        try {
            Email.postEmail("第一封电子邮件");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
