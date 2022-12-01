package com.apxy.courseSystem.entity.vo;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubjectsSituation {
    //答对人数
    private Integer rightCount = 0;
    //正确率
    private BigDecimal rightPercent;
}
