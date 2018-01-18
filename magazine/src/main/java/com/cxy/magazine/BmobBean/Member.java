package com.cxy.magazine.BmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2017/10/4.
 */

public class Member extends BmobObject {
    private User user;
    private String startTime;
    private String finishTime;
    private Double memberMoney;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Double getMemberMoney() {
        return memberMoney;
    }

    public void setMemberMoney(Double memberMoney) {
        this.memberMoney = memberMoney;
    }
}
