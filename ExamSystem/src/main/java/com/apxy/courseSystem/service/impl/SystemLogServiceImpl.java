package com.apxy.courseSystem.service.impl;


import com.apxy.courseSystem.constant.QueryConstant;
import com.apxy.courseSystem.dao.SystemLogDao;

import com.apxy.courseSystem.entity.SystemLogEntity;
import com.apxy.courseSystem.entity.event.MemberEvent;
import com.apxy.courseSystem.entity.event.SystemLogEvent;
import com.apxy.courseSystem.entity.vo.MemberVo;
import com.apxy.courseSystem.entity.vo.SystemLogVo;
import com.apxy.courseSystem.service.RoleService;
import com.apxy.courseSystem.service.RoleUserService;
import com.apxy.courseSystem.service.SystemLogService;
import com.apxy.courseSystem.util.Constant;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemLogServiceImpl extends ServiceImpl<SystemLogDao, SystemLogEntity> implements SystemLogService {


    /**
     * 监听日志输出
     */
    @Async
    @EventListener(SystemLogEvent.class)
    public void doEvent(SystemLogEvent systemLogEvent) {
        log.info("副线程开始啦-----------");
        System.out.println(systemLogEvent.getSystemLogEntity());
        this.save(systemLogEvent.getSystemLogEntity());
        log.info("副线程结束啦-----------");
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String timeOrder = params.get(QueryConstant.TIME_ORDER).toString();

        if (timeOrder.equals("true")) {
            Page<SystemLogVo> ret = new Page<>(Long.parseLong((String) params.get(Constant.PAGE)), Long.parseLong((String) params.get("pageSize")));
            IPage<SystemLogVo> result = this.baseMapper.queryPage(ret);

            return new PageUtils(result);
        }
        Page<SystemLogVo> ret = new Page<>(Long.parseLong((String) params.get(Constant.PAGE)), Long.parseLong((String) params.get("pageSize")));
        IPage<SystemLogVo> result = this.baseMapper.queryPageAnother(ret);


        return new PageUtils(result);
    }

    //获得查询参数的方法
    private String getQueryKey(Map<String, Object> params) {
        return params.get(QueryConstant.QUERY_KEY).toString();
    }

}
