package com.cxy.magazine.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RankListEntity implements Serializable{
    private ArrayList<RankEntity> rankEntityList;

    public ArrayList<RankEntity> getRankEntityList() {
        return rankEntityList;
    }

    public void setRankEntityList(ArrayList<RankEntity> rankEntityList) {
        this.rankEntityList = rankEntityList;
    }
}
