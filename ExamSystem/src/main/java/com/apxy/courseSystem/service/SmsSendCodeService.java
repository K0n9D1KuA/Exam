package com.apxy.courseSystem.service;

public interface SmsSendCodeService {
    public void SendMsg(String email,String subject,String context);
}
