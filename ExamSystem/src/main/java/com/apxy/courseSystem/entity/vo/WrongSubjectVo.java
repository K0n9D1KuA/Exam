package com.apxy.courseSystem.entity.vo;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.entity.SubjectEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class WrongSubjectVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    private Long id;
    /**
     * 关联试题id
     */
    private Long subjectId;
    /**
     * 出错频率
     */
    private BigDecimal wrongFrequency = new BigDecimal("0");
    /**
     * 出错人数
     */
    private Long wrongCount = 0L;
    /**
     * 总做题人数
     */
    private Long totalCount = 0L;
    /**
     * 出错题目及其频率
     */
    private Map<String,Integer> answerFrequency;
    /**
     * 对应试题实体类
     */
    private SubjectEntity subjectEntity;
}
