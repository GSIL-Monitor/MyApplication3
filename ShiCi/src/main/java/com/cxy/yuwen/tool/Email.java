package com.cxy.yuwen.tool;

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

    public  static boolean postEmail(String message){
        System.out.println(message);
       boolean isSuccess=false;

        // 收件人电子邮箱   QQ邮箱
        String to = "caoxingyu2016@qq.com";

        // 发件人电子邮箱   139邮箱
        String from = "caoxingyu2016@139.com";

        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.139.com";  //139 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
       Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("caoxingyu2016@139.com", "941125cxy"); //发件人邮件用户名、密码
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage messages = new MimeMessage(session);

            // Set From: 头部头字段
            messages.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            messages.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头部头字段
            messages.setSubject("语文助手用户反馈!");

            // 设置消息体
            messages.setText(message);

            // 发送消息
            Transport.send(messages);
            System.out.println("Sent message successfully....from 139邮箱");
            isSuccess=true;
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }

        return isSuccess;
    }

    public  static void main(String [] args){
      boolean flag=postEmail("139发往QQ邮箱");
        if (flag){
            System.out.println("发送成功");
        }
    }
}
