package com.cxy.yuwen.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2018/2/6.
 */

public class MsgNotification extends BmobObject {
   private User user;
   private String title;
   private String detail;
   private Boolean isRead;  //是否已读

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
