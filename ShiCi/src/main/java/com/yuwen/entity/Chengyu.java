package com.yuwen.entity;

import java.io.Serializable;

/**
 * Created by cxy on 2016/7/20.
 */
public class Chengyu implements Serializable{
    private  String name;
    private  String pinyin;
    private String  jieshi;
    private String from;
    private String example;
    private String yufa;
    private String yinzheng;
    private String tongyi;
    private String fanyi;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getFanyi() {
        return fanyi;
    }

    public void setFanyi(String fanyi) {
        this.fanyi = fanyi;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getJieshi() {
        return jieshi;
    }

    public void setJieshi(String jieshi) {
        this.jieshi = jieshi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getTongyi() {
        return tongyi;
    }

    public void setTongyi(String tongyi) {
        this.tongyi = tongyi;
    }

    public String getYinzheng() {
        return yinzheng;
    }

    public void setYinzheng(String yinzheng) {
        this.yinzheng = yinzheng;
    }

    public String getYufa() {
        return yufa;
    }

    public void setYufa(String yufa) {
        this.yufa = yufa;
    }
}
