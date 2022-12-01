package com.apxy.courseSystem.entity.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷视图对象
 */
@Data
public class PaperVoEntity implements Serializable {
    private static final long serialVersionUID = 1L;
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
     * 包含题目Id
     */
    private Long[] selectedSubjects;
}
