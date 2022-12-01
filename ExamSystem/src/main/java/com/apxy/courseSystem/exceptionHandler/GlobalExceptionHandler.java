package com.apxy.courseSystem.exceptionHandler;



import com.apxy.courseSystem.exception.LoginException;
import com.apxy.courseSystem.util.R;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //定义异常的处理方法

    @ExceptionHandler(LoginException.class)
    public R LoginExceptionHandler(LoginException e)
    {
          //打印异常信息
         //从异常对象中获取提示信息进行封装
        System.out.println("出现了异常："+e);
        int code = e.getCode();
        String msg = e.getMsg();
        return R.error(code,msg);
    }


}
