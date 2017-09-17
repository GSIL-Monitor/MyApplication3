package com.yuwen.BmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2017/9/15.
 */

public class Collect extends BmobObject{
    public static final Integer ZI=1,CIYU=2,CHENGYU=3,SHICI=4;

    private Integer type;
    private String name;
    private String content;
    private User user;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

