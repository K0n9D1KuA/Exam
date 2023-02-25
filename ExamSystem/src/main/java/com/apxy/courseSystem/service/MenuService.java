package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.MenuEntity;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.apxy.courseSystem.util.usingUtil.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author courseSystem
 * @email 3179735066@qq.com
 * @date 2022-09-02 23:06:31
 */
public interface MenuService extends IService<MenuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MenuEntity> getPermissionsById(Long id);

    List<MenuVo> createTreeMenu(List<MenuEntity> menuEntities);

    List<MenuVo> getMenuTreeList();
}

