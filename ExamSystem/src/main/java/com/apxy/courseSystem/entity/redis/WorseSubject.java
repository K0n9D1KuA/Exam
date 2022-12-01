package com.apxy.courseSystem.entity.redis;


import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//记录错题频率
@Data
public class WorseSubject implements Serializable {
    private static final long serialVersionUID = 1L;
    //关联subjectId
    private Long subjectId;
    // 某个错误答案出现次数 格式: A：5  B：3
    private Map<String, Integer> WrongAnswerFrequency = new HashMap<>();
    //错误次数
    private Integer wrongCount = 0;
    //正确次数
    private Integer rightCount = 0;
}
