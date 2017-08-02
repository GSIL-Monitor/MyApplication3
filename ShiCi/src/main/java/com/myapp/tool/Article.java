package com.myapp.tool;

import java.io.Serializable;

/**
 * Created by cxy on 2016/7/12.
 */
public class Article implements Serializable {
    private String id;
    private String title;
    private String zuoZhe;
    private String content;
    private String jieShao;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJieShao() {
        return jieShao;
    }

    public void setJieShao(String jieShao) {
        this.jieShao = jieShao;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getZuoZhe() {
        return zuoZhe;
    }

    public void setZuoZhe(String zuoZhe) {
        this.zuoZhe = zuoZhe;
    }



}
