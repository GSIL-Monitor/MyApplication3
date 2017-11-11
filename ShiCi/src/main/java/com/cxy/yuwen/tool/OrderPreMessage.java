package com.cxy.yuwen.tool;

import java.io.Serializable;
import java.util.Date;

/**
 * 类描述：订单初始化返回信息
 * <p>
 *     应用场景：创建订单/支付接口返回信息
 * </p>
 * 创建人：XieWQ
 * 创建时间：2017/4/7 0007 下午 15:22
 */
public class OrderPreMessage implements Serializable{
    /**订单号*/
    private String orderId;
    /**本平台提交到微信/支付宝的订单号*/
    private String outTradeNo;
    /**AliPay/WxPay*/
    private String payType;
    /**金额（单位分）*/
    private int consume;
    /**加密好的支付宝订单信息，是能向支付宝直接提交的订单信息。*/
    private String alipayMsg;
    /**商品名称*/
    private String goodsName;
    /**商品描述*/
    private String goodsDesc;
    /**订单创建时间*/
    private Date createTime;

    public OrderPreMessage(){}

    public OrderPreMessage(String orderId, int consume, String payType){
        this.orderId = orderId;
        this.consume = consume;
        this.payType = payType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public String getAlipayMsg() {
        return alipayMsg;
    }

    public void setAlipayMsg(String alipayMsg) {
        this.alipayMsg = alipayMsg;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }
}
