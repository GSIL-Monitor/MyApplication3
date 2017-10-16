package com.yuwen.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2017/10/11.
 */

public class MemberRecharge extends BmobObject {
    private String orderNumber;
    private User user;
    private Double money;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}
