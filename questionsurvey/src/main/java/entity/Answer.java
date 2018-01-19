package entity;

import java.io.Serializable;

/**
 * Created by cxy on 2018/1/5.
 * 答案class
 */

public class Answer implements Serializable{
    private String questionId;
    private String itemNo;
    private String itemContent;

    private static final long serialVersionUID=1L;

    public Answer( String itemNo, String itemContent) {

        this.itemNo = itemNo;
        this.itemContent = itemContent;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemContent() {
        return itemContent;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }
}
