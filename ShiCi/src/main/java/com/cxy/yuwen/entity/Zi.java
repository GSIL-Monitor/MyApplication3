package com.cxy.yuwen.entity;

import java.io.Serializable;

/**
 * Created by cxy on 2017/9/2.
 */

public class Zi implements Serializable {
    private  String id;
    private  String hanzi;
    private  String pinyin;
    private  String duyin;
    private  String bushou;
    private  String bihua;
    private  String jianjie;
    private  String xiangjie;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHanzi() {
        return hanzi;
    }

    public void setHanzi(String hanzi) {
        this.hanzi = hanzi;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getDuyin() {
        return duyin;
    }

    public void setDuyin(String duyin) {
        this.duyin = duyin;
    }

    public String getBushou() {
        return bushou;
    }

    public void setBushou(String bushou) {
        this.bushou = bushou;
    }

    public String getBihua() {
        return bihua;
    }

    public void setBihua(String bihua) {
        this.bihua = bihua;
    }

    public String getJianjie() {
        jianjie=jianjie.replace("<br>","\n");
        return jianjie;
    }

    public void setJianjie(String jianjie) {
        this.jianjie = jianjie;
    }

    public String getXiangjie() {
        xiangjie=xiangjie.replace("<br>","\n");
        return xiangjie;
    }

    public void setXiangjie(String xiangjie) {
        this.xiangjie = xiangjie;
    }

    @Override
    public String toString() {
        return "Zi{" +
                "id='" + id + '\'' +
                ", hanzi='" + hanzi + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", duyin='" + duyin + '\'' +
                ", bushou='" + bushou + '\'' +
                ", bihua='" + bihua + '\'' +
                ", jianjie='" + jianjie + '\'' +
                ", xiangjie='" + xiangjie + '\'' +
                '}';
    }
}
