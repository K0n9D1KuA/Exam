package com.apxy.courseSystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author courseSystem
 * @email 3179735066@qq.com
 * @date 2022-09-02 23:06:31
 */
@Data
@TableName("menu")
public class MenuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private List<MenuEntity> children;
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 菜单名称
     */
    private String label;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 菜单名称，方便路由名字跳转
     */
    private String name;
    /**
     * 菜单路劲
     */
    private String url;
    /**
     * 路由根据path跳转
     */
    private String path;
    /**
     * 父菜单id，如果是一级菜单，默认为0
     */
    private Long parentId;
    /**
     * 菜单功能描述
     */
    private String menuDescription;

    /**
     * 权限拥有者
     */

    @TableField(exist = false)
    private String owner;
}
