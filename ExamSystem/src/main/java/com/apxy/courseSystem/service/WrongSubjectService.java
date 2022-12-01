package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.WrongSubjectEntity;
import com.apxy.courseSystem.entity.vo.WrongSubjectVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface WrongSubjectService extends IService<WrongSubjectEntity> {
    List<WrongSubjectVo> getAll();
}
