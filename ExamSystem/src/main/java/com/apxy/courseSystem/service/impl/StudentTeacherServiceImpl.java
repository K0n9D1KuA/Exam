package com.apxy.courseSystem.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.apxy.courseSystem.dao.StudentTeacherDao;
import com.apxy.courseSystem.entity.StudentTeacherEntity;
import com.apxy.courseSystem.service.StudentTeacherService;
import com.apxy.courseSystem.util.usingUtil.PageUtils;
import com.apxy.courseSystem.util.usingUtil.Query;

@Service("studentTeacherService")
public class StudentTeacherServiceImpl extends ServiceImpl<StudentTeacherDao, StudentTeacherEntity> implements StudentTeacherService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StudentTeacherEntity> page = this.page(
                new Query<StudentTeacherEntity>().getPage(params),
                new QueryWrapper<StudentTeacherEntity>()
        );

        return new PageUtils(page);
    }

}
