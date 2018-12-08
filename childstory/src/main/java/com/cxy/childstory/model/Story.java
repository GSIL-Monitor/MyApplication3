package com.cxy.childstory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Cteated by cxy on 2018/10/19
 */
public class Story implements Parcelable{


    private String id;
    //标题
    private String title;
    //分类
    private String type;
    //概要
    private String summary;
    //内容
    private String content;
    //图片路径
    private String imagePath;
    //音频路径数组
    private List<String> audioPaths;

    //章节List:第一章、第二章
    private List<String> childStoryIds;


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<String> getAudioPaths() {
        return audioPaths;
    }

    public void setAudioPaths(List<String> audioPaths) {
        this.audioPaths = audioPaths;
    }

    public List<String> getChildStoryIds() {
        return childStoryIds;
    }

    public void setChildStoryIds(List<String> childStoryIds) {
        this.childStoryIds = childStoryIds;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.type);
        dest.writeString(this.summary);
        dest.writeString(this.content);
        dest.writeString(this.imagePath);
        dest.writeStringList(this.audioPaths);
        dest.writeStringList(this.childStoryIds);
    }

    public Story() {
    }

    protected Story(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.type = in.readString();
        this.summary = in.readString();
        this.content = in.readString();
        this.imagePath = in.readString();
        this.audioPaths = in.createStringArrayList();
        this.childStoryIds = in.createStringArrayList();
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
