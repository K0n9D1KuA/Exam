package com.apxy.courseSystem.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//生命周期
@Retention(RetentionPolicy.RUNTIME)
//在方法上
@Target(ElementType.METHOD)
public @interface SystemLog {
    //接口业务名字
    String businessName();
}
