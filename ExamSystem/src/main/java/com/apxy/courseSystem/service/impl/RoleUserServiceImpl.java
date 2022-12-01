package com.apxy.courseSystem.service.impl;

import com.apxy.courseSystem.dao.RoleUserDao;
import com.apxy.courseSystem.entity.RoleUser;
import com.apxy.courseSystem.service.RoleUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserDao, RoleUser> implements RoleUserService {
}
