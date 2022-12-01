package com.apxy.courseSystem.service.impl;

import com.apxy.courseSystem.dao.MenuDao;
import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.MenuEntity;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.apxy.courseSystem.service.MemberService;

import com.apxy.courseSystem.service.MenuService;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuDao, MenuEntity> implements MenuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MenuEntity> page = this.page(
                new Query<MenuEntity>().getPage(params),
                new QueryWrapper<MenuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MenuEntity> getPermissionsById(Long id) {
        MenuDao baseMapper = this.baseMapper;
        List<MenuEntity> memberEntityList = baseMapper.getPermissionsById(id);
        return memberEntityList;
    }

    /**
     * 组装成树形菜单
     *
     * @param menuEntities
     * @return
     */
    @Override
    public List<MenuVo> createTreeMenu(List<MenuEntity> menuEntities) {
        //首先获得所有得到一级菜单
        List<MenuEntity> oneLevelMenu = menuEntities.stream().filter(o -> o.getParentId().intValue() == 0).collect(Collectors.toList());
        //遍历找到其子菜单
        List<MenuEntity> ret = oneLevelMenu.stream().map(o -> {
            o.setChildren(this.getChildren(o, menuEntities));
            return o;
        }).collect(Collectors.toList());
        return ret.stream().map(o -> {
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(o, menuVo);
            return menuVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MenuVo> getMenuTreeList() {
        return this.createTreeMenu(this.list());
    }

    /**
     * 根据当前菜单查询其子菜单并组装
     *
     * @param root
     * @param all
     * @return
     */
    private List<MenuEntity> getChildren(MenuEntity root, List<MenuEntity> all) {
        //找到了root所有的子菜单  再为子菜单找到其子菜单
        List<MenuEntity> children = all.stream().filter(o -> o.getParentId().longValue() == root.getId().longValue()).map(o ->
        {
            o.setChildren(this.getChildren(o, all));
            return o;
        }).collect(Collectors.toList());
        return children;
    }
}
