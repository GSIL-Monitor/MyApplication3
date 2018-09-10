package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2018/8/27.
 */

public class RecommBean extends BmobObject {
    private ArticleRecommBean articleRecommBean;
    private User user;
    //评论
    private String comment;

    public ArticleRecommBean getArticleRecommBean() {
        return articleRecommBean;
    }

    public void setArticleRecommBean(ArticleRecommBean articleRecommBean) {
        this.articleRecommBean = articleRecommBean;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
