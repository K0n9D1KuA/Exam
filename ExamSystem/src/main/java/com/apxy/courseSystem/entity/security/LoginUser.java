package com.apxy.courseSystem.entity.security;


import com.alibaba.fastjson.annotation.JSONField;
import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.MenuEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    private MemberEntity memberEntity;

    public LoginUser(MemberEntity studentEntity, List<String> permissions) {
        this.memberEntity = studentEntity;
        this.permissions = permissions;
    }

    //存储权限信息
    private List<String> permissions;

    //存储SpringSecurity所需要的权限信息的集合
    @JSONField(serialize = false)
    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }

            authorities = permissions.stream().
                    map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return authorities;


    }

    @Override
    public String getPassword() {
        return memberEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return memberEntity.getUsreName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
