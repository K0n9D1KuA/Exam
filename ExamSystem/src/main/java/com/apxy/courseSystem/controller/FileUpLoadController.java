package com.apxy.courseSystem.controller;

import com.apxy.courseSystem.entity.DoneSubject;

import com.apxy.courseSystem.entity.MemberEntity;

import com.apxy.courseSystem.entity.security.LoginUser;

import com.apxy.courseSystem.service.DoneSubjectService;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.service.UploadService;
import com.apxy.courseSystem.util.R;

import com.apxy.courseSystem.util.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * 上传文件接口
 */
@RestController

public class FileUpLoadController {
    @Autowired
    UploadService uploadService;
    @Autowired
    MemberService memberService;
    @Autowired
    SpringSecurityUtil springSecurityUtil;
    @Autowired
    DoneSubjectService doneSubjectService;



    //上传题目图片
    @RequestMapping("/uploadFile/subjectImage")
//    @PreAuthorize("hasAuthority('/home')")
    public R uploadSubjectImage(@RequestParam("file") MultipartFile[] files) throws IOException {
        String s = uploadService.upLoadFile(files[0]);
        System.out.println(s);
        return R.ok().put("data", s);
    }


    //上传用户头像
    @RequestMapping("/uploadFile/avatar")
    @PreAuthorize("hasAuthority('/home')")
    public R uploadAvatar(@RequestParam("file") MultipartFile[] files) throws IOException {

        String avatarUrl = uploadService.uploadAvatar(files[0]);
        return R.ok().put("data", avatarUrl);
    }

    /**
     * 学生上传答案接口
     */
    @RequestMapping("/uploadFile/uploadDoneSubjectImage/{currentIndex}/{memberName}")
    @PreAuthorize("hasAuthority('/home')")
    public R uploadDoneSubjectImage(@RequestParam("file") MultipartFile[] files, @PathVariable Long currentIndex, @PathVariable String memberName) throws IOException {

        uploadService.uploadDoneSubjectImage(files[0], currentIndex, memberName);
        return R.ok();
    }

    //上传学生信息表
    @RequestMapping("/uploadFile/StudentExcel")
    @PreAuthorize("hasAuthority('/allList')")
    public R uploadStudentExcel(@RequestParam("file") MultipartFile[] files) throws IOException {
        uploadService.uploadStudentExcel(files[0]);
        return R.ok();
    }

    //上老师
    @RequestMapping("/uploadFile/teacherExcel")
    @PreAuthorize("hasAuthority('/allList')")
    public R uploadTeacherExcel(@RequestParam("file") MultipartFile[] files) throws IOException {
        uploadService.uploadTeacherExcel(files[0]);
        return R.ok();
    }

}
