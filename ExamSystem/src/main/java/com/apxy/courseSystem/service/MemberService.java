package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.excel.Member;
import com.apxy.courseSystem.util.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author courseSystem
 * @email 3179735066@qq.com
 * @date 2022-09-02 23:06:31
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    public void sendCode(String email, String subject, String context);


    String getRoleById(Integer id);

    void saveStudentByExcel(List<Member> cachedDataList);

    void saveTeacherByExcel(List<Member> cachedDataList);

    List<MemberEntity> getAllStudents();

    PageUtils queryAllStudents(Map<String, Object> params);

    PageUtils queryAllStudentsWithTeacher(Map<String, Object> params);

    PageUtils getAllTeacher(Map<String, Object> params);


}

