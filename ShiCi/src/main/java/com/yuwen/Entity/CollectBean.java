package com.yuwen.Entity;


/**
 * Created by cxy on 2017/9/3.
 */

public class CollectBean {

private int id;
private String type;
private String name;
private String content;
private String createTime;
private String updateTime;

    public CollectBean(int id, String type, String name, String content, String createTime, String updateTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CollectBean{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
