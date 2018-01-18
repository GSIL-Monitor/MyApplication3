package com.cxy.yuwen.tool;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by cxy on 2018/1/14.
 * 邮件认证类
 */

public class MailAuthenticator extends Authenticator {
    String userName = null;
    String password = null;
    public MailAuthenticator() {
    }
    public MailAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }




}
