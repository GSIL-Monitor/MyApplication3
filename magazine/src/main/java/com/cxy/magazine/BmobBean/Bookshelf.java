package com.cxy.magazine.BmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2018/1/3.
 */

public class Bookshelf extends BmobObject {
    private User user;
    private String bookName;
    private String publishTime;
    private String coverUrl;
    private String directoryUrl;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPulishTime() {
        return publishTime;
    }

    public void setPulishTime(String pulishTime) {
        this.publishTime = pulishTime;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDirectoryUrl() {
        return directoryUrl;
    }

    public void setDirectoryUrl(String directoryUrl) {
        this.directoryUrl = directoryUrl;
    }
}
