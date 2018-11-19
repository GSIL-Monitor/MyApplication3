package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * 系统消息的阅读记录
 */
public class MsgReadRecord extends BmobObject {

    private MsgNotification msgNotification;
    private User user;

    public MsgNotification getMsgNotification() {
        return msgNotification;
    }

    public void setMsgNotification(MsgNotification msgNotification) {
        this.msgNotification = msgNotification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
