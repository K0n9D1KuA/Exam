package com.apxy.courseSystem.service.impl;

import com.alibaba.excel.EasyExcel;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;


import com.apxy.courseSystem.constant.AliyunOssConstant;
import com.apxy.courseSystem.entity.MemberEntity;

import com.apxy.courseSystem.entity.event.DoneSubjectImageEvent;
import com.apxy.courseSystem.entity.event.MemberEvent;
import com.apxy.courseSystem.entity.excel.Member;
import com.apxy.courseSystem.excelLisenner.StudentExcelLisener;
import com.apxy.courseSystem.excelLisenner.TeacherExcelLisener;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.service.UploadService;
import com.apxy.courseSystem.util.SpringSecurityUtil;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService, ApplicationContextAware {
    @Autowired
    AliyunOssConstant aliyunOssConstant;
    ApplicationContext applicationContext;
    @Autowired
    private SpringSecurityUtil springSecurityUtil;
    @Autowired
    private MemberService memberService;

    /**
     * 上传图片到阿里云oss   返回值 图片地址
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String upLoadFile(MultipartFile file) throws IOException {
        String endpoint = aliyunOssConstant.endpoint;
        String accessKeyId = aliyunOssConstant.keyId;
        String accessKeySecret = aliyunOssConstant.keySecret;
        String bucketName = aliyunOssConstant.bucketName;
        String s = UUID.randomUUID().toString().replaceAll("-", "");
        //生成随机文件名字 防止覆盖
        String filename = s + file.getOriginalFilename();
        String url = null;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = file.getInputStream();
            // 创建PutObject请求。
            // 参数1 buckName  参数2 上传到oss文件路径和文件名称
            // 上传文件输入流

            //把文件按照日期分类 2022/8/17
            //获取当前时期
            String datePath = new DateTime().toString("yyyy/MM/dd");
            //拼接
            filename = datePath + "/" + filename;
            ossClient.putObject(bucketName, filename, inputStream);
            ossClient.shutdown();
            url = "https://" + bucketName + "." + endpoint + "/" + filename;
            return url;

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        return null;
    }

    /**
     * 监听学生上传题目答案图片业务 从redis中修改
     * 图片的存储格式  http:dsjaiojdsaijdfjsafa;http:dsjaiojdsaijdfjsafa;
     * @param
     */
    @Override
    public void uploadDoneSubjectImage(MultipartFile file, Long currentIndex, String memberName) {
        try {
            String imageUrl = this.upLoadFile(file);
            //发布事件
            applicationContext.publishEvent(new DoneSubjectImageEvent(imageUrl, this, currentIndex, memberName));
            System.out.println("主业务执行完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //用户上传头像
    @Override
    public String uploadAvatar(MultipartFile file) {
        String avatarUrl = "";
        try {
            avatarUrl = this.upLoadFile(file);
            MemberEntity memberEntity = springSecurityUtil.getUser().getMemberEntity();
            memberEntity.setAvatar(avatarUrl);
            //发布事件 解耦
            applicationContext.publishEvent(new MemberEvent(memberEntity, this));
            return avatarUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return avatarUrl;
    }

    /**
     * excel导入学生信息
     * @param file
     */
    @Override
    public void uploadStudentExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), Member.class, new StudentExcelLisener(memberService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * excel导入老师信息
     * @param file
     */
    @Override
    public void uploadTeacherExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), Member.class, new TeacherExcelLisener(memberService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发布事件容器
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
