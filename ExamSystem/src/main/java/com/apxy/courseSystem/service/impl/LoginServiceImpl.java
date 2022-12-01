package com.apxy.courseSystem.service.impl;

import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.MenuEntity;
import com.apxy.courseSystem.entity.security.LoginUser;
import com.apxy.courseSystem.service.LoginService;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MenuService menuService;

    /**
     * 集成springSecurity
     * @param username 用户名
     * @return  UserDetails 里面存了密码 和 用户权限信息
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberService.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsreName, username));
        if (Objects.isNull(memberEntity)) {
            //说明用户名不存在
            throw new RuntimeException("用户名不存在");
        }
        //封装权限信息
        //根据用户id查询用户的权限
        Long id = memberEntity.getId().longValue();
        //根据用户id查询用户的权限
        List<MenuEntity> menuEntities = menuService.getPermissionsById(id);
        menuEntities =  menuEntities.parallelStream().filter(Objects::nonNull).collect(Collectors.toList());
        if (menuEntities != null && menuEntities.size() > 0) {
            memberEntity.setMenuEntities(menuEntities);
            //获得该用户的权限
            List<String> permissions = menuEntities.stream().map(o -> {
                return o.getPath().toString();
            }).collect(Collectors.toList());
            return new LoginUser(memberEntity, permissions);
        } else {
            return new LoginUser(memberEntity, null);
        }
    }
}
