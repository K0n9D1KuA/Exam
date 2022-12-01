package com.apxy.courseSystem.constant;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "exam.thread")
@Component
@Data
public class ThreadPoolConfigConstant {
    /**
     * 核心线程数量
     */
    private Integer coreSize;
    /**
     * 最大线程数量
     */
    private Integer maxSize;
    /**
     * 存活时间 单位：s
     */
    private Integer keepAliveTime;
}
