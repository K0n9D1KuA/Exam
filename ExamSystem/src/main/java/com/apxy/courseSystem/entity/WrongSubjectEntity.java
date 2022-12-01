package com.apxy.courseSystem.entity;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@TableName("wrong_subject")
public class WrongSubjectEntity implements Serializable {

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
    private String wrongAnswerFrequency = JSON.toJSONString(new HashMap<String, Integer>());
}
