package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2018/8/27.
 */

public class ArticleRecommBean extends BmobObject {
    private String articleUrl;
    private String articleTitle;
    private String articleTime;
    private String articleId;
    //赞总数
    private Integer praiseCount;
    //推荐次数
    private Integer recommCount;

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

    public String getArticleTime() {
        return articleTime;
    }

    public void setArticleTime(String articleTime) {
        this.articleTime = articleTime;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public Integer getRecommCount() {
        return recommCount;
    }

    public void setRecommCount(Integer recommCount) {
        this.recommCount = recommCount;
    }

    public Integer getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(Integer praiseCount) {
        this.praiseCount = praiseCount;
    }
}
