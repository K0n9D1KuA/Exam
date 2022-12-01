package com.apxy.courseSystem.service.impl;


import com.apxy.courseSystem.service.SmsSendCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmsSendCodeServiceImpl implements SmsSendCodeService {
    //邮件发送人 从配置文件中读取
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void SendMsg(String email, String subject, String context) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //从哪里发？
        simpleMailMessage.setFrom(from);
        //发送给谁
        simpleMailMessage.setTo(email);
        //标题
        simpleMailMessage.setSubject(subject);
        //内容
        simpleMailMessage.setText(context);
        //执行真正的发邮件操作
        mailSender.send(simpleMailMessage);
    }
}
