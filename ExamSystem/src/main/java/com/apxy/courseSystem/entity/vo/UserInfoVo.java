package com.apxy.courseSystem.entity.vo;


import com.apxy.courseSystem.entity.MenuEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 用户名
     */
    private String usreName;
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
     * 角色
     */
    private String role;

    /**
     * 头像
     */
    private String avatar;
}
