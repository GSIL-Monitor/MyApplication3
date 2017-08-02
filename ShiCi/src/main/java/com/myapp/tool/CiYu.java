package com.myapp.tool;

import java.io.Serializable;

/**
 * Created by cxy on 2017/4/22.
 */

public class CiYu implements Serializable {
    private String name;
    private String content;

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
}
