package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.SystemLogEntity;
import com.apxy.courseSystem.util.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SystemLogService extends IService<SystemLogEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
