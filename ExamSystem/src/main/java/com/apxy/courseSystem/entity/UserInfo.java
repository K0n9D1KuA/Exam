package com.apxy.courseSystem.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserInfo {
    //code: "11"
    //email: "1715653502@qq.com"
    //password: "123456"
    //username: "dasjijj"

    //验证码
    @NotEmpty(message = "验证码必须填写")
    private String code;
    //邮箱

    @NotEmpty(message = "邮箱不能为空")
    @Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$", message = "邮箱格式不正确")
    private String email;
    //密码
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;
    //用户名
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
    private String username;
}
