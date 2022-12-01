package com.apxy.courseSystem.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


/**
 * 做过的试卷
 */
@TableName("done_paper")
@Data
@ToString
public class DonePaperEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 试卷名称
     */
    private String paperName;
    /**
     * 开始时间
     */
    private Date beginTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 总共持续时间 以分钟为单位
     */
    private Integer totalTime;
    /**
     * 总分
     */
    private Integer totalScore;
    /**
     * 关联老师id
     */
    private Long teacherId;
    /**
     * 做试卷学生姓名
     */
    private Long studentId;
    /**
     * 实际总分
     */
    private Integer actualScore = 0;
    /**
     * 关联试卷id
     */
    private Long paperId;
    /**
     * 试卷完成情况 [0:已完成 1:未完成]
     */
    private Integer paperType;



}
