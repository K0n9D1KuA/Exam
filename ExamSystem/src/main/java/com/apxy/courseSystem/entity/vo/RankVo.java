package com.apxy.courseSystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RankVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 排名
     */
    private Integer position;
    /**
     * 分数
     */
    private Integer score;
}
