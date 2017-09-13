package com.yuwen.BmobBean;

import cn.bmob.v3.BmobUser;

/**
 * Created by cxy on 2017/9/10.
 */

public class User extends BmobUser {
    private String place;
    private String person;

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
}
