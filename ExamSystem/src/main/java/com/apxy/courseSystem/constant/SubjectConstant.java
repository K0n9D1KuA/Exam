package com.apxy.courseSystem.constant;


/**
 * 题目类常量
 */
public class SubjectConstant {
    //题目类型 0-单选 1-多选题 2-填空题 3-判断题 4-大题
    //单选题
    public static final Integer SINGLE_SUBJECT = 0;
    //多选题
    public static final Integer NO_SINGLE_SUBJECT = 1;
    //填空题
    public static final Integer FILL_SUBJECT = 2;
    //填空题
    public static final Integer JUDGE_SUBJECT = 3;
    //大题
    public static final Integer SHORT_ANSWER_SUBJECT = 4;
    //redis key
    public static final String WRONG_ANSWER_FREQUENCY_PREFIX_KEY = "wrong:answer:frequency:";

}
