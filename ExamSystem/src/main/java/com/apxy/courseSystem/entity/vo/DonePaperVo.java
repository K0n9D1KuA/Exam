package com.apxy.courseSystem.entity.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DonePaperVo implements Serializable {
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
    /**
     * 做题学生
     */
    private String studentName;

    /**
     * 出题老师
     */
    private String teacherName;
}
