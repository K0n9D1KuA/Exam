package com.apxy.courseSystem.controller;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.service.SystemLogService;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/systemLog")
public class SystemLogController {
    /**
     * 列表
     */
    @Autowired
    private SystemLogService systemLogService;

    @RequestMapping("/list")
    //@RequiresPermissions("courseSystem:subject:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = systemLogService.queryPage(params);

        return R.ok().put("page", page);
    }

    @Autowired
    SystemLogService service;

    /**
     * 删除日志
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("courseSystem:subject:delete")
    public R delete(@RequestBody String ids) {
        List<Long> systemLogIds = JSON.parseArray(JSON.parseObject(ids).getString("ids"), Long.class);
        System.out.println("haha");
        systemLogService.removeByIds(systemLogIds);
        return R.ok();
    }

}
