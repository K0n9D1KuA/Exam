package com.apxy.courseSystem.exception.security;

import com.alibaba.fastjson.JSON;

import com.apxy.courseSystem.util.usingUtil.R;
import com.apxy.courseSystem.util.usingUtil.WebUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 认证失败的统一处理配置
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //相应给前端
        //打印一下异常信息 方便以后定位
        e.printStackTrace();
        //BadCredentialsException
        //用户名或者密码错误返回的异常
        //InsufficientAuthenticationException
        //需要登录返回的异常
        //那么这里需要做一个判断  来给用户更好的提示
        if (e instanceof BadCredentialsException) {
            WebUtil.renderString(httpServletResponse, JSON.toJSONString(R.error(401, e.getMessage())));
        } else if (e instanceof InsufficientAuthenticationException) {
            WebUtil.renderString(httpServletResponse, JSON.toJSONString(R.error(402, "需要登录后才能访问！")));
        } else {
            //其他情况
            WebUtil.renderString(httpServletResponse, JSON.toJSONString(R.error(500, "用户名不存在！")));
        }


    }
}
