package com.apxy.courseSystem.dao;

import com.apxy.courseSystem.entity.Role;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleDao extends BaseMapper<Role> {
    List<MenuVo> getAuthority(@Param("id") Long id);

}
