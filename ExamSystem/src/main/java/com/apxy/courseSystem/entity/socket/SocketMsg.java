package com.apxy.courseSystem.entity.socket;


import lombok.Data;

@Data
public class SocketMsg {
    /**
     * 聊天类型 0 群聊 1 单聊
     **/
    private int type;
    /**
     * 发送者
     **/
    private String sendOutUser;
    /**
     * 接受者
     **/
    private String receiveUser;
    /**
     * 消息
     **/
    private String msg;
    /**
     * 身份   0 代表老师 1 代表学生
     */
    private Integer identity;
    /**
     * 试卷id
     */
    private Long paperId;
}
