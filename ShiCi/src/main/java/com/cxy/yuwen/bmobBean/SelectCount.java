package com.cxy.yuwen.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2017/10/12.
 */

public class SelectCount extends BmobObject {
    public static final Integer ZI=1,CIYU=2,CHENGYU=3,SHICI=4,COMPOSITION=5;
    private User user;
    private Integer selectType;
    private String selectDate;
    private Integer selectCount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSelectType() {
        return selectType;
    }

    public void setSelectType(Integer selectType) {
        this.selectType = selectType;
    }

    public String getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String selectDate) {
        this.selectDate = selectDate;
    }

    public Integer getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(Integer selectCount) {
        this.selectCount = selectCount;
    }
}
