package com.cxy.yuwen.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2018/2/5.
 */

public class InviteCode extends BmobObject {
    private User user;
    private String inviteCode;
    private Boolean isActivate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Boolean getActivate() {
        return isActivate;
    }

    public void setActivate(Boolean activate) {
        isActivate = activate;
    }
}
