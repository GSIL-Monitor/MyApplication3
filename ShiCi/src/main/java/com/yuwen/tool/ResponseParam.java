package com.yuwen.tool;

/**
 * 类描述：响应对象
 * 创建人：XieWQ
 * 创建时间：2017/4/7 0007 下午 16:55
 */
public class ResponseParam<T> {

    /** 提示信息 */
    private String msg;
    /** 请求是否处理成功 */
    private boolean status;
    /** 错误码 */
    private int code;
    /** 返回对象 */
    private T data;

    public ResponseParam(){}

    /**
     * 初始化响应对象-响应出错时
     * @param code
     * @param msg
     */
    public ResponseParam(int code, String msg){
        this.status = false;
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
