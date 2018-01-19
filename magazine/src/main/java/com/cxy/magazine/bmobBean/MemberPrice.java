package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cxy on 2017/12/23.
 */

public class MemberPrice extends BmobObject {
    private Integer monthSum;
    private Double originalPrice;
    private Double currentPrice;
    private String remark;

    public Integer getMonthSum() {
        return monthSum;
    }

    public void setMonthSum(Integer monthSum) {
        this.monthSum = monthSum;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
