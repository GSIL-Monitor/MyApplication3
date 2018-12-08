package com.cxy.childstory.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxy on 2018/12/4
 */

public class StoryType implements Parcelable {
    private String id;
    private String name;
    private String imageUrl;

    public StoryType(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public StoryType() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
    }

    protected StoryType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<StoryType> CREATOR = new Parcelable.Creator<StoryType>() {
        @Override
        public StoryType createFromParcel(Parcel source) {
            return new StoryType(source);
        }

        @Override
        public StoryType[] newArray(int size) {
            return new StoryType[size];
        }
    };
    @Override
    public String toString() {
        return "StoryType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
