package com.apxy.courseSystem.aspect;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.annotation.SystemLog;
import com.apxy.courseSystem.entity.SystemLogEntity;
import com.apxy.courseSystem.entity.event.SystemLogEvent;
import com.apxy.courseSystem.entity.vo.UserInfoVo;
import com.apxy.courseSystem.util.usingUtil.R;
import com.apxy.courseSystem.util.usingUtil.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 日志aop切面类
 * @email 3161788646@qq.com
 * @date 2023/1/9 22:25
 */

@Aspect
@Component
@Slf4j
public class LogAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    //切点
    @Pointcut("@annotation(com.apxy.courseSystem.annotation.SystemLog)")
    public void pt() {

    }

    /**
     * 环绕通知
     */
    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret;
        try {
            SystemLogEntity systemLogEntity = new SystemLogEntity();
            before(joinPoint, systemLogEntity);
            ret = joinPoint.proceed();//将异常往外抛  会被自定义的异常处理器处理掉
            //给日志对象赋值
            ret(ret, systemLogEntity);
            System.out.println(systemLogEntity);
//            applicationContext.publishEvent();
        } finally {
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());
        }
        return ret;
    }

    private void ret(Object ret, SystemLogEntity systemLogEntity) {
        // 打印出参
        log.info("Response       : {}", JSON.toJSONString(ret));
        //类型转换
        R r = (R) ret;
        UserInfoVo userInfo = (UserInfoVo) r.get("userInfo");
        systemLogEntity.setUserName(userInfo.getMemberName());
        log.info("来自于ip:{}的{}用户于:{}时刻访问了url:{} 其访问接口功能为:{} ", systemLogEntity.getIp(), systemLogEntity.getUserName(), systemLogEntity.getNowTime(), systemLogEntity.getUrl(), systemLogEntity.getBusinessName());
        //发布事件 通知异步更新日志
        applicationContext.publishEvent(new SystemLogEvent(this, systemLogEntity));
        log.info("主线程完毕啦---------------------------------");
    }

    private void before(ProceedingJoinPoint joinPoint, SystemLogEntity systemLogEntity) {
        systemLogEntity.setNowTime(new Date());
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        //获取加在方法上的注解
        SystemLog systemLog = getSystemLog(joinPoint);
        systemLogEntity.setBusinessName(systemLog.businessName());
        //请求的url
        systemLogEntity.setUrl(request.getRequestURI());
        // 打印 Http method
        if (request != null) {
            systemLogEntity.setMethod("GET".equals(request.getMethod()) ? 0 : 1);
        }
        // 打印请求的 IP
        systemLogEntity.setIp(request.getRemoteHost());

    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(SystemLog.class);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
