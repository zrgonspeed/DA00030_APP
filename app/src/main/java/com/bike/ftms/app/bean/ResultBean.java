package com.bike.ftms.app.bean;

public class ResultBean {
    private int httpCode;
    private int httpMessage;

    private String code;
    private String message;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpMessage() {
        return httpMessage;
    }

    public void setHttpMessage(int httpMessage) {
        this.httpMessage = httpMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "httpCode=" + httpCode +
                ", httpMessage=" + httpMessage +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
