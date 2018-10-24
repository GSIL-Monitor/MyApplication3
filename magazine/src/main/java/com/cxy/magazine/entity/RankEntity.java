package com.cxy.magazine.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RankEntity implements Serializable{
    private String title;  //一周 一月 一年
    private ArrayList<HashMap<String,String>> data;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<HashMap<String, String>> getData() {
        return data;
    }

    public void setData(ArrayList<HashMap<String, String>> data) {
        this.data = data;
    }
}
