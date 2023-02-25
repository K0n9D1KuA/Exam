package com.apxy.courseSystem.controller;

import java.util.Arrays;
import java.util.Map;


import com.apxy.courseSystem.util.usingUtil.PageUtils;
import com.apxy.courseSystem.util.usingUtil.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apxy.courseSystem.entity.StudentPaparEntity;
import com.apxy.courseSystem.service.StudentPaparService;




/**
 *
 *
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@RestController
@RequestMapping("courseSystem/studentpapar")
public class StudentPaparController {
    @Autowired
    private StudentPaparService studentPaparService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("courseSystem:studentpapar:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = studentPaparService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("courseSystem:studentpapar:info")
    public R info(@PathVariable("id") Integer id){
		StudentPaparEntity studentPapar = studentPaparService.getById(id);

        return R.ok().put("studentPapar", studentPapar);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("courseSystem:studentpapar:save")
    public R save(@RequestBody StudentPaparEntity studentPapar){
		studentPaparService.save(studentPapar);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("courseSystem:studentpapar:update")
    public R update(@RequestBody StudentPaparEntity studentPapar){
		studentPaparService.updateById(studentPapar);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("courseSystem:studentpapar:delete")
    public R delete(@RequestBody Integer[] ids){
		studentPaparService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
