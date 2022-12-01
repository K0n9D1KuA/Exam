package com.apxy.courseSystem.util;

import com.apxy.courseSystem.entity.security.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * SpringSecurity工具类 便于获得当前登录得用户的信息
 */
@Component
public class SpringSecurityUtil {
    //获得当前用户
    public LoginUser getUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser;
    }

    //获得当前用户的用户名
    //获得当前用户
    public String getUserName() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getMemberEntity().getMemberName();
    }
}
