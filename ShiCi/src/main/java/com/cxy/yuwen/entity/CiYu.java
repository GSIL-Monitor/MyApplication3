package com.cxy.yuwen.entity;

import java.io.Serializable;

/**
 * Created by cxy on 2017/4/22.
 */

public class CiYu implements Serializable {
    private String name;
    private String content;
    private String id;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CiYu{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
