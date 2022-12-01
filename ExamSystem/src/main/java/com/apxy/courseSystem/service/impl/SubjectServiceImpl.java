package com.apxy.courseSystem.service.impl;

import com.apxy.courseSystem.constant.QueryConstant;
import com.apxy.courseSystem.constant.SubjectConstant;
import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.vo.SubjectVo;
import com.apxy.courseSystem.entity.vo.SubjectVoEntity;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.util.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.apxy.courseSystem.dao.SubjectDao;
import com.apxy.courseSystem.entity.SubjectEntity;
import com.apxy.courseSystem.service.SubjectService;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.Query;

@Service("subjectService")
public class SubjectServiceImpl extends ServiceImpl<SubjectDao, SubjectEntity> implements SubjectService {
    @Autowired
    private SpringSecurityUtil springSecurityUtil;
    @Autowired
    private MemberService memberService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = params.get("key").toString();
        Integer subjectType = Integer.valueOf(params.get(QueryConstant.SUBJECT_TYPE).toString());
        LambdaQueryWrapper<SubjectEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //是否拼装题目类型限制
        if (subjectType != 6) {
            //需要进行题目类型的限制
            lambdaQueryWrapper.eq(SubjectEntity::getSubjectType, subjectType);
        }
        if (key != null && !"".equals(key)) {
            lambdaQueryWrapper.like(SubjectEntity::getSubjectName, key);
        }
        lambdaQueryWrapper.eq(SubjectEntity::getTeacherId, springSecurityUtil.getUser().getMemberEntity().getId());
        IPage<SubjectEntity> page = this.page(
                new Query<SubjectEntity>().getPage(params),
                lambdaQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSubject(SubjectEntity subject) {
        Integer id = springSecurityUtil.getUser().getMemberEntity().getId();
        subject.setTeacherId(id.longValue());
        this.save(subject);
    }

    @Override
    public PageUtils allSubjectList(Map<String, Object> params) {
        String key = params.get(QueryConstant.QUERY_KEY).toString();
        Integer subjectType = Integer.valueOf(params.get(QueryConstant.SUBJECT_TYPE).toString());
        LambdaQueryWrapper<SubjectEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //是否拼装题目类型限制
        if (subjectType != 6) {
            //需要进行题目类型的限制
            lambdaQueryWrapper.eq(SubjectEntity::getSubjectType, subjectType);
        }
        //是否拼装查询参数
        if (key != null && !"".equals(key)) {
            lambdaQueryWrapper.like(SubjectEntity::getSubjectName, key);
        }
        IPage<SubjectEntity> page = this.page(
                new Query<SubjectEntity>().getPage(params),
                lambdaQueryWrapper
        );
        //封装
        IPage<SubjectVo> ret = new Page<>();
        BeanUtils.copyProperties(page, ret, "records");
        return new PageUtils(ret.setRecords(page.getRecords().stream().map(o -> {
            SubjectVo subjectVo = new SubjectVo();
            BeanUtils.copyProperties(o, subjectVo);
            //获得老师id
            Long teacherId = o.getTeacherId();
            MemberEntity teacher = memberService.getById(teacherId);
            //设置老师姓名
            subjectVo.setTeacherName(teacher.getMemberName());
            return subjectVo;
        }).collect(Collectors.toList())));
    }

}
