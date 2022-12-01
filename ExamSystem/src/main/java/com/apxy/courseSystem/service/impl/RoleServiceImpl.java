package com.apxy.courseSystem.service.impl;

import com.apxy.courseSystem.dao.RoleDao;
import com.apxy.courseSystem.entity.Role;
import com.apxy.courseSystem.entity.RoleMenuEntity;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.apxy.courseSystem.service.RoleMenuService;
import com.apxy.courseSystem.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {
    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 根据角色id查询起对应的权限信息
     *
     * @param id
     * @return
     */
    @Override
    public List<MenuVo> getAuthority(Long id) {
        RoleDao baseMapper = this.getBaseMapper();
        List<MenuVo> authority = baseMapper.getAuthority(id);
        authority = authority.stream().filter(o -> {
            return !Objects.isNull(o);
        }).collect(Collectors.toList());
        ;
        if (authority.size() == 0) {
            return null;
        }
        //处理一下
        List<RoleMenuEntity> list = roleMenuService.list(new LambdaQueryWrapper<RoleMenuEntity>().eq(RoleMenuEntity::getRoleId, id));
        list = list.stream().filter(o -> {
            return !Objects.isNull(o);
        }).collect(Collectors.toList());
        if (list != null && list.size() > 0) {
            //收集他们的id
            List<Long> menuIds = list.stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList());
            authority.forEach(o -> {
                if (menuIds.contains(o.getId())) {
                    //说明包含
                    o.setHasOrNotHas(true);
                } else {
                    //说明不包含
                    o.setHasOrNotHas(false);
                }
            });
        }
        return authority;
    }
}
