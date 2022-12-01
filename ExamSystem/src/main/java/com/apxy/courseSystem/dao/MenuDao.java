package com.apxy.courseSystem.dao;

import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.MenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface MenuDao extends BaseMapper<MenuEntity> {
    List<MenuEntity> getPermissionsById(Long id);
}
