package com.apxy.courseSystem.entity.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PaperVo implements Serializable {
    /**
     * 主键
     */
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
     * 关联老师姓名
     */
    private String teacherName;
    /**
     * 关联老师id
     */
    private Long teacherId;
}
