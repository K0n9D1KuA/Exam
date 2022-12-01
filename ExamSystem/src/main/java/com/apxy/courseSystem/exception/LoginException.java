package com.apxy.courseSystem.exception;

public class LoginException extends RuntimeException {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LoginException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;

    }
}
