package com.apxy.courseSystem.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 日志aop自定义注解
 * @email 3161788646@qq.com
 * @date 2023/1/9 22:25
 */

//生命周期 运行时
@Retention(RetentionPolicy.RUNTIME)
//在方法上
@Target(ElementType.METHOD)
public @interface SystemLog {
    //接口业务名字
    String businessName();
}
