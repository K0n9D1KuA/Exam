package com.apxy.courseSystem.dao;

import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.excel.Member;
import com.apxy.courseSystem.entity.vo.MemberVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    String getRoleById(Integer id);

    IPage<MemberVo> getAllStudentsWithTeacher(Page<MemberVo> ret);

    List<Long> getTeacherIds();

    IPage<MemberEntity> getTeachers(Page<MemberEntity> ret);



}
