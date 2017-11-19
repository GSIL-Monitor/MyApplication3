package com.cxy.yuwen.tool;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cxy on 2017/11/19.
 */

public class ParcelableMap implements Parcelable {
    private HashMap paramMap;

    public ParcelableMap(HashMap paramMap) {
        this.paramMap = paramMap;
    }

    public HashMap getParamMap() {
        return paramMap;
    }

    public void setParamMap(HashMap paramMap) {
        this.paramMap = paramMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(paramMap);
    }

    // 反序列过程：必须实现Parcelable.Creator接口，并且对象名必须为CREATOR
    // 读取Parcel里面数据时必须按照成员变量声明的顺序，Parcel数据来源上面writeToParcel方法，读出来的数据供逻辑层使用
    public static final Parcelable.Creator<ParcelableMap> CREATOR=new Creator<ParcelableMap>() {
        @Override
        public ParcelableMap createFromParcel(Parcel source) {
            return new ParcelableMap(source.readHashMap(HashMap.class.getClassLoader()));
        }

        @Override
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };
}
