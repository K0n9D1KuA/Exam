package com.apxy.courseSystem.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.apxy.courseSystem.dao.StudentPaparDao;
import com.apxy.courseSystem.entity.StudentPaparEntity;
import com.apxy.courseSystem.service.StudentPaparService;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.Query;

@Service("studentPaparService")
public class StudentPaparServiceImpl extends ServiceImpl<StudentPaparDao, StudentPaparEntity> implements StudentPaparService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StudentPaparEntity> page = this.page(
                new Query<StudentPaparEntity>().getPage(params),
                new QueryWrapper<StudentPaparEntity>()
        );

        return new PageUtils(page);
    }

}