package com.apxy.courseSystem.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("role")
@Data
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 角色名
     */
    private String role;
    /**
     * 角色权限描述
     */
    private String roleDescription;
}
