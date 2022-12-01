package com.apxy.courseSystem.dao;

import com.apxy.courseSystem.entity.SystemLogEntity;
import com.apxy.courseSystem.entity.vo.SystemLogVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface SystemLogDao extends BaseMapper<SystemLogEntity> {

    IPage<SystemLogVo> queryPage(Page<SystemLogVo> ret);

    IPage<SystemLogVo> queryPageAnother(Page<SystemLogVo> ret);

}
