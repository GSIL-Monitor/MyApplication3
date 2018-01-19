package entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cxy on 2018/1/5.
 * 问题class
 */

public class Question implements Serializable{
    private String id;
    private String questionNo;       //题目编号
    private String questionContent;  //题目内容
    private String type;             //题目类型
    private List<Answer> answers;

    private static final long serialVersionUID=1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
