package com.cxy.magazine.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateMagazine implements Parcelable {
    private String title;
    private String href;

    protected UpdateMagazine(Parcel in) {
        title = in.readString();
        href = in.readString();
    }
    public static final Creator<UpdateMagazine> CREATOR = new Creator<UpdateMagazine>() {
        @Override
        public UpdateMagazine createFromParcel(Parcel in) {
            return new UpdateMagazine(in);
        }

        @Override
        public UpdateMagazine[] newArray(int size) {
            return new UpdateMagazine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(href);
    }

    public UpdateMagazine() {
    }

    public UpdateMagazine(String title, String href) {
        this.title = title;
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
