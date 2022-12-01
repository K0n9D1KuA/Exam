package com.apxy.courseSystem.enuem;


/**
 * 枚举类 登录异常
 */
public enum LoginEnuem {
    VALID_ERROR(78912,"数据校验出错"),
    CODE_OVERDUE(78913,"验证码已过期"),
    CODE_ERROR(78914,"验证码错误"),
    REGISTER_FAILURE(78915,"注册失败！"),
    USERNAME_EXIST_ERROR(78916,"用户名已存在！"),
    LOGIN_FAILURE(78918,"登录失败！"),
    EMAIL_EXIST_ERROR(78917,"邮箱已存在！"),
    NEED_LOGIN(78918,"需要重新登录!"),
    WRONG_PASSWORD(78919,"密码错误!");
    private int code;
    private String msg;
    LoginEnuem(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
