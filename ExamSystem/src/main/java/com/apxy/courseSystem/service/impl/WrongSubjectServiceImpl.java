package com.apxy.courseSystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.apxy.courseSystem.constant.WrongSubjectConstant;
import com.apxy.courseSystem.dao.WrongSubjectDao;
import com.apxy.courseSystem.entity.SubjectEntity;
import com.apxy.courseSystem.entity.WrongSubjectEntity;
import com.apxy.courseSystem.entity.vo.WrongSubjectVo;
import com.apxy.courseSystem.service.SubjectService;
import com.apxy.courseSystem.service.WrongSubjectService;
import com.apxy.courseSystem.util.usingUtil.SpringSecurityUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class WrongSubjectServiceImpl extends ServiceImpl<WrongSubjectDao, WrongSubjectEntity> implements WrongSubjectService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<WrongSubjectVo> getAll() {

        //获得当前老师姓名
        String teacherName = SpringSecurityUtil.getUser().getMemberEntity().getMemberName();
        //获得redis的key
        String key = WrongSubjectConstant.WRONG_SUBJECT_PREFIX_KEY + teacherName;
        //去redis中查询数据
        Boolean aBoolean = stringRedisTemplate.hasKey(key);
        List<WrongSubjectEntity> retList = new ArrayList<>();
        if (BooleanUtil.isTrue(aBoolean)) {
            //说明有数据 直接返回
            //获得题目的数量
            Long size = stringRedisTemplate.opsForList().size(key);
            //获得所有题目
            List<String> range = stringRedisTemplate.opsForList().range(key, 0, size - 1);
            List<WrongSubjectVo> wrongSubjectVos = range.stream().map(o -> {
                return JSON.parseObject(o, WrongSubjectVo.class);
            }).collect(Collectors.toList());
            Collections.shuffle(wrongSubjectVos);
            return wrongSubjectVos.subList(0, 4);
        }
        //说明没有
        //从数据库中拿道数据
        List<WrongSubjectEntity> list = this.list();
        //获得所有的题目
        List<SubjectEntity> subjectEntities = subjectService.list();
        List<WrongSubjectVo> wrongSubjectVoList = list.stream().map(o -> {
            WrongSubjectVo wrongSubjectVo = new WrongSubjectVo();
            BeanUtil.copyProperties(o, wrongSubjectVo);
            wrongSubjectVo.setSubjectEntity(subjectEntities.stream().filter(j -> {
                return j.getId().equals(o.getSubjectId());
            }).collect(Collectors.toList()).get(0));
            wrongSubjectVo.setAnswerFrequency(JSON.parseObject(o.getWrongAnswerFrequency(), new TypeReference<HashMap<String, Integer>>() {
            }));
            stringRedisTemplate.opsForList().rightPush(key, JSON.toJSONString(wrongSubjectVo));
            return wrongSubjectVo;
        }).collect(Collectors.toList());
        Collections.shuffle(wrongSubjectVoList);
        return wrongSubjectVoList.subList(0, 4);
    }



}


