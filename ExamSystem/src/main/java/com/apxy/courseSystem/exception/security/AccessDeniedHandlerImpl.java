package com.apxy.courseSystem.exception.security;

import com.alibaba.fastjson.JSON;

import com.apxy.courseSystem.util.R;
import com.apxy.courseSystem.util.WebUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这里是授权失败的异常处理器
 */

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        //相应给前端
        //打印一下异常信息 方便以后定位
        e.printStackTrace();

        WebUtil.renderString(httpServletResponse, JSON.toJSONString(R.error(401, "sorry 你没有权限访问该接口！")));
    }
}
