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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private MenuService menuService;

    /**
     * 查看所有角色
     */
    @GetMapping("/getAllRoles")
    public R getAllRoles() {
        return R.ok().put("data", roleService.list());
    }

    /**
     * 查询角色还未分配的权限
     */
    @GetMapping("/getAuthority/{id}")
    public R getAuthority(@PathVariable Long id) {
        List<MenuVo> ret = roleService.getAuthority(id);
        return R.ok().put("data", ret);

    }

    /**
     * 给某个角色分配权限
     */
    @PostMapping("/deliverAuthorityToOneRole")
    public R deliverAuthorityToOneRole(@RequestBody RoleMenuEntity roleMenuEntity) {
        //获得角色id
        Long roleId = roleMenuEntity.getRoleId();
        roleMenuService.save(roleMenuEntity);
        //同理还需要绑定父菜单和角色的关系
        //获得父id
        MenuEntity byId = menuService.getById(roleMenuEntity.getMenuId());
        Long parentId = byId.getParentId();
        //健壮性判断
        LambdaQueryWrapper<RoleMenuEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenuEntity::getMenuId, parentId).eq(RoleMenuEntity::getRoleId, roleId);
        //看是否已经绑定过了
        RoleMenuEntity one = roleMenuService.getOne(lambdaQueryWrapper);
        if (Objects.isNull(one)) {
            //说明没有绑定过
            RoleMenuEntity roleMenuEntity1 = new RoleMenuEntity();
            roleMenuEntity1.setRoleId(roleId);
            roleMenuEntity1.setMenuId(parentId);
            roleMenuService.save(roleMenuEntity1);
        }
        return R.ok();
    }

    /**
     * 移除某个角色的权限
     */
    @PostMapping("/removeAuthorityToOneRole")
    public R removeAuthorityToOneRole(@RequestBody RoleMenuEntity roleMenuEntity) {
        //菜单id
        Long menuId = roleMenuEntity.getMenuId();
        //角色id
        Long roleId = roleMenuEntity.getRoleId();
        //同理还需要绑定父菜单和角色的关系
        //获得父id
        MenuEntity menuEntity = menuService.getById(menuId);
        //获得父id
        Long parentId = menuEntity.getParentId();
        //健壮性判断
        //获得所有的子菜单
        List<MenuEntity> childMenuEntities = menuService.list(new LambdaQueryWrapper<MenuEntity>().eq(MenuEntity::getParentId, parentId));
        List<Long> menuIds = childMenuEntities.stream().map(MenuEntity::getId).collect(Collectors.toList());
        List<RoleMenuEntity> roleMenuEntities = roleMenuService.list(new LambdaQueryWrapper<RoleMenuEntity>().in(RoleMenuEntity::getMenuId, menuIds).eq(RoleMenuEntity::getRoleId, roleId));
        if (roleMenuEntities.size() == 1) {
            //那么需要删除父菜单
            roleMenuService.remove(new LambdaQueryWrapper<RoleMenuEntity>().eq(RoleMenuEntity::getMenuId, parentId).eq(RoleMenuEntity::getRoleId, roleId));
        }
        //删除
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenuEntity>().eq(RoleMenuEntity::getMenuId, menuId).eq(RoleMenuEntity::getRoleId, roleId));
        return R.ok();
    }
}
