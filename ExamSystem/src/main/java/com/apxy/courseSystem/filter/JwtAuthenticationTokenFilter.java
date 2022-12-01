package com.apxy.courseSystem.filter;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.security.LoginUser;
import com.apxy.courseSystem.enuem.LoginEnuem;
import com.apxy.courseSystem.util.JwtUtil;
import com.apxy.courseSystem.util.R;
import com.apxy.courseSystem.util.WebUtil;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 拦截器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private String LOGIN_KEY = AuthServerConstant.LOGIN_USER;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //获得请求头中的token
        String token = httpServletRequest.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //如果请求头中没有token  直接放行
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            //什么时候会出现这个异常呢？
            //1,token过期
            //2,token非法
            //相应告诉前端需要重新登录
            WebUtil.renderString(httpServletResponse, JSON.toJSONString(R.error(LoginEnuem.NEED_LOGIN.getCode(), LoginEnuem.NEED_LOGIN.getMsg())));
            return;
        }
        //的解析获取uuid
        String uuid = claims.getSubject();

        //从redis中获得用户信息
        String key = LOGIN_KEY + uuid;
        String jsonString = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(jsonString)) {
            //说明redis中的过期了 需要重新登录
            WebUtil.renderString(httpServletResponse, JSON.toJSONString(R.error(LoginEnuem.NEED_LOGIN.getCode(), LoginEnuem.NEED_LOGIN.getMsg())));
            return;
        }
        //从redis中获得用户信息
        LoginUser loginUser = JSON.parseObject(jsonString,LoginUser.class);
        //将用户信息存入SecurityContextHolder中
        //认证状态下的要用三个参数的构造方法
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        //过滤器执行完了需要放行
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        return;

    }
}
