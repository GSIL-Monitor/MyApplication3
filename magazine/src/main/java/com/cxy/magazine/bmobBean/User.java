package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobUser;

/**
 * Created by cxy on 2017/9/10.
 */

public class User extends BmobUser {

    private Integer userType;
    private String headImageUrl;
    public static final Integer REGISTER_USER=1,QQ_USER=2;



    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }
}
