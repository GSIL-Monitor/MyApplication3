package com.cxy.yuwen.bmobBean;

import cn.bmob.v3.BmobUser;

/**
 * Created by cxy on 2017/9/10.
 */

public class User extends BmobUser {
    private String place;
    private String person;
    private Integer userType;
    public static final Integer REGISTER_USER=1,QQ_USER=2;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
