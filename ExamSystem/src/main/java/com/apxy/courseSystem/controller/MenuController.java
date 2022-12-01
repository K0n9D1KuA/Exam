package com.apxy.courseSystem.controller;


import com.apxy.courseSystem.entity.MenuEntity;
import com.apxy.courseSystem.entity.Role;
import com.apxy.courseSystem.entity.RoleMenuEntity;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.apxy.courseSystem.service.MenuService;
import com.apxy.courseSystem.service.RoleMenuService;
import com.apxy.courseSystem.service.RoleService;
import com.apxy.courseSystem.util.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private RoleService roleService;


    @GetMapping("/getTreeList")
    public R menuTreeList() {
        List<MenuVo> menuVoList = menuService.getMenuTreeList();

        return R.ok().put("data", menuVoList);
    }

    /**
     * 根据菜单查询
     */
    @GetMapping("/menuInfo/{menuId}")
    public R getMenuInfo(@PathVariable Long menuId) {
        MenuEntity byId = menuService.getById(menuId);
        List<RoleMenuEntity> list = roleMenuService.list(new LambdaQueryWrapper<RoleMenuEntity>().eq(RoleMenuEntity::getMenuId, menuId));
        List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>().in(Role::getId, list.stream().map(RoleMenuEntity::getRoleId).collect(Collectors.toList())));
        List<String> tmp = new ArrayList<>();
        roles.forEach(o -> {
            tmp.add(o.getRole());
        });
        String tmpS = "";
        for (int i = 0; i < tmp.size(); i++) {
            if (i == tmp.size() - 1) {
                tmpS += tmp.get(i);
            } else {
                tmpS += tmp.get(i) + ",";
            }
        }
        byId.setOwner(tmpS);
        return R.ok().put("data", byId);
    }

    /**
     * 新增二级菜单
     */
    @PostMapping("/addTwoMenu")
    public R addMenu(@RequestBody MenuVo menuVo) {
        //获得path路劲
        String path = menuVo.getPath();
        String[] split = path.split("/");
        MenuEntity menuEntity = new MenuEntity();
        //获得得父菜单
        MenuEntity fatherMenu = menuService.getById(menuVo.getParentId());
        //获得其path路径
        String fatherPath = fatherMenu.getPath();
        String[] split1 = fatherPath.split("/");
        String url = split1[1] + path;
        menuEntity.setUrl(url);
        menuEntity.setName(split[1]);
        menuEntity.setIcon("document-copy");
        menuEntity.setPath(menuVo.getPath());
        menuEntity.setParentId(menuVo.getParentId());
        menuEntity.setLabel(menuVo.getLabel());
        menuEntity.setMenuDescription(menuVo.getMenuDescription());
        menuService.save(menuEntity);
        return R.ok();
    }

    /**
     * 新增一级菜单
     */
    @PostMapping("/addOneMenu")
    public R addOneMenu(@RequestBody MenuVo menuVo) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setIcon("document-copy");
        menuEntity.setPath(menuVo.getPath());
        menuEntity.setParentId(0L);
        menuEntity.setLabel(menuVo.getLabel());
        menuService.save(menuEntity);
        return R.ok();
    }


}
