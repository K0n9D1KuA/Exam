package com.apxy.courseSystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@Data
@TableName("member")
public class MemberEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单实体类
     */
    @TableField(exist = false)
    private List<MenuEntity> menuEntities;
    /**
     * 主键
     */
    @TableId
    private Integer id;
    /**
     * 用户名
     */
    private String usreName;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 性别[男：1 女：0]
     */
    private Integer sex;
    /**
     * 入学年份
     */
    private Date entryTime;
    /**
     * 专业
     */
    private String profession;
    /**
     * 用户名称
     */
    private String memberName;

    /**
     * 用户头像
     */
    private String avatar;

}
