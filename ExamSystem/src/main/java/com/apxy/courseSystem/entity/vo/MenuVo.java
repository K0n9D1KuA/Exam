package com.apxy.courseSystem.entity.vo;


import com.apxy.courseSystem.entity.MenuEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    private Long Id;
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
     * 孩子菜单
     */
    private List<MenuEntity> children;
    /**
     * 菜单功能描述
     */
    private String menuDescription;
    /**
     * 父亲id
     */
    private Long parentId;
    /**
     * 是否拥有
     */
    private Boolean hasOrNotHas;
}
