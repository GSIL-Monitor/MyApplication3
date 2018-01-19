package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2018/1/19.
 */

public class CollectBean extends BmobObject {
    private User user;
    private String articleUrl;
    private String articleTitle;
    private String artivleId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArtivleId() {
        return artivleId;
    }

    public void setArtivleId(String artivleId) {
        this.artivleId = artivleId;
    }
}
