package com.apxy.courseSystem.constant;

/**
 * 登录相关常量
 */
public class AuthServerConstant {
    //缓存验证码
    public static final String SMS_CODE_CACHE_PREFIX = "register:code:";
    //缓存用户信息
    public static final String LOGIN_USER = "login:userInfo:";
    //验证码内容
    public static final String CONTEXT = "欢迎使用上海第二工业大学在线考试系统，验证码将于五分钟后过期，请妥善保管 验证码：";
    //邮箱标题
    public static final String subject = "上海第二工业大学在线考试系统注册验证码";
    //存储试卷的key
    public static final String PAPER_PREFIX_KEY = "login:userInfo:paper:";
    //用户默认头像
    public static final String DEFAULT_AVATAR_URL = "https://wcity.oss-cn-hangzhou.aliyuncs.com/u%3D1224875187%2C62470964%26fm%3D253%26fmt%3Dauto%26app%3D138%26f%3DPNG.webp";
    //学生角色名
    public static final String STUDENT_ROLE_NAME = "student";
    //老师角色名
    public static final String TEACHER_ROLE_NAME = "teacher";


}
