package com.apxy.courseSystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    /**
     * 上传图片到阿里云oss   返回值 图片地址
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String upLoadFile(MultipartFile file) throws IOException;

    /**
     * 学生上传题目图片
     *
     * @param file         图片
     * @param currentIndex 第几题？
     * @param memberName   学生姓名
     */
    void uploadDoneSubjectImage(MultipartFile file, Long currentIndex, String memberName);

    /**
     * 用户上传头像
     *
     * @param file
     * @return
     */
    String uploadAvatar(MultipartFile file);

    /**
     * excel导入学生信息
     *
     * @param file excel文件
     */
    void uploadStudentExcel(MultipartFile file);

    /**
     * excel导入老师信息
     *
     * @param file file excel文件
     */
    void uploadTeacherExcel(MultipartFile file);
}
