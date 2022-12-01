package com.apxy.courseSystem.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.constant.AliyunOssConstant;
import com.apxy.courseSystem.constant.AuthServerConstant;

import com.apxy.courseSystem.entity.DonePracticeSubject;
import com.apxy.courseSystem.entity.DoneSubject;
import com.apxy.courseSystem.entity.SubjectPaper;
import com.apxy.courseSystem.entity.vo.DoneSubjectVo;
import com.apxy.courseSystem.entity.vo.SubjectVoEntity;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.R;
import com.apxy.courseSystem.util.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.apxy.courseSystem.entity.SubjectEntity;
import com.apxy.courseSystem.service.SubjectService;

import javax.swing.*;


/**
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@RestController
@RequestMapping("courseSystem/subject")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    AliyunOssConstant aliyunOssConstant;
    //    @Autowired
//    OSS ossClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SpringSecurityUtil springSecurityUtil;


    /**
     * 列表
     */
    @PreAuthorize("hasAuthority('/majorSubject')")
    @RequestMapping("/list")
    //@RequiresPermissions("courseSystem:subject:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = subjectService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @PreAuthorize("hasAuthority('/majorSubject')")
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("courseSystem:subject:info")
    public R info(@PathVariable("id") Long id) {
        SubjectEntity subject = subjectService.getById(id);

        return R.ok().put("subject", subject);
    }

    /**
     * 保存
     */
    @PreAuthorize("hasAuthority('/majorSubject')")
    @RequestMapping("/save")
    // @RequiresPermissions("courseSystem:subject:save")
    public R save(@RequestBody SubjectEntity subject) {

        subjectService.saveSubject(subject);

        return R.ok();
    }

    /**
     * 修改
     */
    @PreAuthorize("hasAuthority('/majorSubject')")
    @RequestMapping("/update")
    // @RequiresPermissions("courseSystem:subject:update")
    public R update(@RequestBody SubjectEntity subject) {
        subjectService.updateById(subject);

        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('/majorSubject')")
    @RequestMapping("/delete")
    // @RequiresPermissions("courseSystem:subject:delete")
    public R delete(@RequestBody String ids) {
        List<Long> subjectIds = JSON.parseArray(JSON.parseObject(ids).getString("ids"), Long.class);
        System.out.println("haha");
        subjectService.removeByIds(subjectIds);
        return R.ok();
    }


    //查看学校题库 //totalSubject
    @PreAuthorize("hasAuthority('/totalSubject')")
    @RequestMapping("/allSubjectList")
    //@RequiresPermissions("courseSystem:subject:list")
    public R allSubjectList(@RequestParam Map<String, Object> params) {
        PageUtils page = subjectService.allSubjectList(params);

        return R.ok().put("page", page);
    }


    /**
     * 组成一套模拟卷
     *
     * @param subjectTypes
     * @return
     */
    @PostMapping("/generatingPapers")
    public R generatingPapers(@RequestBody List<Long> subjectTypes) {
        List<SubjectEntity> list = subjectService.list(new LambdaQueryWrapper<SubjectEntity>().in(SubjectEntity::getSubjectType, subjectTypes));
        //封装
        String key = AuthServerConstant.PAPER_PREFIX_KEY + this.springSecurityUtil.getUserName();
        Collections.shuffle(list);

        return R.ok().put("data", list.stream().map(o -> {
            SubjectVoEntity subjectVoEntity = new SubjectVoEntity();
            BeanUtils.copyProperties(o, subjectVoEntity);
            stringRedisTemplate.opsForList().rightPush(key, JSON.toJSONString(o));
            stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
            return subjectVoEntity;

        }).collect(Collectors.toList()));
    }

    /**
     * 提交模拟练习
     */

    @GetMapping("/submitSubject")
    public R submitSubject() {
        String key = AuthServerConstant.PAPER_PREFIX_KEY + this.springSecurityUtil.getUserName();
        //获得所有题目
        //首先获得size
        Long size = stringRedisTemplate.opsForList().size(key);
        //获得所有的题目
        List<String> range = stringRedisTemplate.opsForList().range(key, 0, size - 1);
        //反序列化
        //定义总分
        int totalScore = 0;
        //定义真实得分

        List<DoneSubjectVo> subjectVoEntityList = range.stream().map(o -> {

            DoneSubjectVo doneSubjectVo = new DoneSubjectVo();
            //反序列化
            SubjectVoEntity subjectVoEntity = JSON.parseObject(o, SubjectVoEntity.class);
            //属性赋值
            BeanUtils.copyProperties(subjectVoEntity, doneSubjectVo);
            //判断一下
            if (doneSubjectVo.getSubjectAnwser().equals(doneSubjectVo.getSelectAnswer())) {
                //说明这道题做对了
                doneSubjectVo.setActualScore(doneSubjectVo.getScore().intValue());
            }
            else
            {
                doneSubjectVo.setActualScore(0);
            }
            return doneSubjectVo;
        }).collect(Collectors.toList());

        //删除redis中的数据
        stringRedisTemplate.delete(key);
        //返回数据
        int actualScore = 0;
        Long studentId = Long.valueOf(springSecurityUtil.getUser().getMemberEntity().getId());
        //算一下得了多少分 并且存入数据库中
        List<DonePracticeSubject> donePracticeSubjects = new ArrayList<>();

        for (DoneSubjectVo o : subjectVoEntityList
        ) {
            DonePracticeSubject donePracticeSubject = new DonePracticeSubject();
            actualScore += o.getActualScore();
            BeanUtils.copyProperties(o,donePracticeSubject);
            //设置学生id
            donePracticeSubject.setStudentId(studentId);
            donePracticeSubjects.add(donePracticeSubject);
        }
        //批量存储

        return R.ok().put("totalScore", totalScore).put("actualScore", actualScore).put("data", subjectVoEntityList);
    }
}
