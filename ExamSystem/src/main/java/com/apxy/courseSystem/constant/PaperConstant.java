package com.apxy.courseSystem.constant;

/**
 * 试卷类常量
 */
public class PaperConstant {
//    试卷情况[0:未完成 1:已完成]

    //试卷批改状态已完成
    public static final Integer UNCOMPLETED = 0;

    //试卷批改状态 未完成
    public static final Integer COMPLETED = 1;

    //学生做过题
    public static final Integer DO_PAPER = 0;

    //学生没有做过提
    public static final Integer NOT_DO_PAPER = 1;

    //可做题间隔时间 30分钟内可重复答题
    public static final Integer DO_PAPER_GAP = 30;
}
