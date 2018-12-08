package com.cxy.childstory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cxy on 2018/11/23
 * 故事章节
 */
public class StorySection implements Parcelable{
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
    }

    public StorySection() {
    }

    protected StorySection(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.type = in.readString();
        this.summary = in.readString();
        this.content = in.readString();
        this.imagePath = in.readString();
        this.audioPaths = in.createStringArrayList();
    }

    public static final Creator<StorySection> CREATOR = new Creator<StorySection>() {
        @Override
        public StorySection createFromParcel(Parcel source) {
            return new StorySection(source);
        }

        @Override
        public StorySection[] newArray(int size) {
            return new StorySection[size];
        }
    };
}
