package com.yuwen.entity;

import java.io.Serializable;

/**
 * Created by cxy on 2017/10/6.  作文
 */

public class Composition implements Serializable {
    private String name;
    private String grade;
    private String type;
    private String word;
    private String level;
    private String writer;
    private int id;
    private String content;
    private String comment;   //点评
    private String school;    //作者学校
    private String teacher;   //点评老师
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Composition{" +
                "name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                ", type='" + type + '\'' +
                ", word='" + word + '\'' +
                ", level='" + level + '\'' +
                ", writer='" + writer + '\'' +
                ", id=" + id +
                ", content='" + content + '\'' +
                ", comment='" + comment + '\'' +
                ", school='" + school + '\'' +
                ", teacher='" + teacher + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
