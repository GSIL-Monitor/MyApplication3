package com.cxy.magazine.emailUtil;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by cxy on 2018/1/14.
 * 基本功能：发送邮件
 */

public class EmailUtil {
    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "587";
    private static final String FROM_ADD = "1746569077@qq.com"; //发送方邮箱
    private static final String FROM_PSW = "tiuwrzvfkdgbegbd";//发送方邮箱授权码，并非密码
    private static final String TO_ADD = "1746569077@qq.com"; //接收方邮箱  自己给自己发

//    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "teprinciple@163.com";
//    private static final String FROM_PSW = "teprinciple163";
////    private static final String TO_ADD = "2584770373@qq.com";

    public static void send(final File file, String toAdd){
        final MailSenderInfo mailInfo = creatMail(toAdd);
        final SimpleMailSender sms = new SimpleMailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }

    public static boolean send(String content){
        final MailSenderInfo mailInfo = creatMail(content);
        final SimpleMailSender sms = new SimpleMailSender();
        return sms.sendTextMail(mailInfo);

    }

    @NonNull
    private static MailSenderInfo creatMail(String content) {
        final MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱
        mailInfo.setToAddress(TO_ADD); // 发到哪个邮件去
        mailInfo.setSubject("杂志天下用户反馈"); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }


}
