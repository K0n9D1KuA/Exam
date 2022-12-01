package com.apxy.courseSystem;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableAsync //开启异步注解功能
@EnableTransactionManagement(proxyTargetClass = true)//开启事务
@EnableScheduling // 开启定时功能
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CourseSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseSystemApplication.class);
    }
}
