package com.apxy.courseSystem.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SystemLogVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 访问者姓名
     */
    private String userName;
    /**
     * 访问者ip
     */
    private String ip;
    /**
     * 访问接口功能
     */
    private String businessName;
    /**
     * 访问方法 get/post? [0:get,1:post]
     */
    private Integer method;

    /**
     * 访问url
     */
    private String url;
    /**
     * 访问时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nowTime;

    /**
     * 角色名
     */

    private String role;
}
