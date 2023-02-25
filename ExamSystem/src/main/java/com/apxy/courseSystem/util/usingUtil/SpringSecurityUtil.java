package com.apxy.courseSystem.util.usingUtil;

import com.apxy.courseSystem.entity.security.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * SpringSecurity工具类 便于获得当前登录得用户的信息
 */

public class SpringSecurityUtil {
    //获得当前用户
    public static LoginUser getUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser;
    }

    //获得当前用户的用户名
    public static String getUserName() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getMemberEntity().getMemberName();
    }
}
