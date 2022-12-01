package com.apxy.courseSystem.entity.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubjectVoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 题目描述
     */
    private String subjectName;
    /**
     * A选项描述
     */
    @JsonProperty("aDescribtion")
    private String aDescribtion;
    /**
     * B选项描述
     */
    @JsonProperty("bDescribtion")
    private String bDescribtion;
    /**
     * C选项描述
     */
    @JsonProperty("cDescribtion")
    private String cDescribtion;
    /**
     * D选项描述
     */
    @JsonProperty("dDescribtion")
    private String dDescribtion;
    /**
     * 题目图片地址
     */
    private String subjectImage;
    /**
     * 题目类型 0-单选 1-多选题 2-填空题 3-判断题 4-大题
     */
    private Integer subjectType;
    /**
     * 题目答案
     */
    private String subjectAnwser;
    /**
     * 题目分数
     */
    private Long score;
    /**
     * 所选择答案
     */
    private String selectAnswer = "";

    /**
     *
     */
    private Boolean doOrNoDo = false;


    /**
     * 所选答案数组
     */
    private String[] selectedChoice = new String[]{};
}
