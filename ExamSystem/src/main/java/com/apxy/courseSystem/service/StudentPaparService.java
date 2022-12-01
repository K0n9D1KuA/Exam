package com.apxy.courseSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.entity.StudentPaparEntity;

import java.util.Map;

/**
 * 
 *
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
public interface StudentPaparService extends IService<StudentPaparEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

