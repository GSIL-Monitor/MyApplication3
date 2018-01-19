package entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cxy on 2018/1/5.
 */

public class Questionnaire implements Serializable{
    private String id;
    private String title;  //试卷名称
    private String version;  //试卷版本
    private String remark;   //试卷备注
    private List<Question> questions;   //题目数组

    private static final long serialVersionUID=1L;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return this.title+this.version;
    }
}
