package com.apxy.courseSystem.config;


import com.apxy.courseSystem.constant.ThreadPoolConfigConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 创建线程池
 */
@Configuration
public class MyThreadConfig {
    /**
     * 核心线程数量20
     * 最大线程数量200
     * 拒绝策略 调用线程执行满出来的任务
     * @param ThreadPoolConfigConstant 配置文件类
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigConstant threadPoolConfigConstant) {
        return new ThreadPoolExecutor(
                threadPoolConfigConstant.getCoreSize(),
                threadPoolConfigConstant.getMaxSize(),
                threadPoolConfigConstant.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
