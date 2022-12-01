package com.apxy.courseSystem.constant;

import java.math.BigDecimal;

/**
 * 已做试卷常量类
 * 好的，≥90％优，≥80％良，≥70％中，≥60％及格，＜60％不及格
 */
public class DonePaperConstant {
    //≥90％优
    public final static BigDecimal ExcellentProportion = new BigDecimal("0.9");
    //≥80％良
    public final static BigDecimal GoodProportion = new BigDecimal("0.8");
    //≥70％中
    public final static BigDecimal MediumProportion = new BigDecimal("0.7");
    //≥60％及格
    public final static BigDecimal PassProportion = new BigDecimal("0.6");
}
