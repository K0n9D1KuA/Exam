package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.Role;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<MenuVo> getAuthority(Long id);
}
