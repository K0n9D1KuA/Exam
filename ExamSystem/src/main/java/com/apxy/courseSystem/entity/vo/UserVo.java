package com.apxy.courseSystem.entity.vo;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;


/*
登录时传来的对象
 */
@Data
public class UserVo {

    //密码
    @NotEmpty(message = "密码不能为空")
    @Length(min = 5, max = 18, message = "密码必须是5-18位字符")
    private String password;
    //用户名
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 5, max = 18, message = "用户名必须是5-18位字符")
    private String username;
}
