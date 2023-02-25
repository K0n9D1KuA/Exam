package com.apxy.courseSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.apxy.courseSystem.util.usingUtil.PageUtils;
import com.apxy.courseSystem.entity.StudentTeacherEntity;

import java.util.Map;

/**
 *
 *
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
public interface StudentTeacherService extends IService<StudentTeacherEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

