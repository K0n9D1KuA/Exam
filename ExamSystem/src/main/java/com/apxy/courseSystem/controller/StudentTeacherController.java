package com.apxy.courseSystem.controller;

import java.util.Arrays;
import java.util.Map;


import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apxy.courseSystem.entity.StudentTeacherEntity;
import com.apxy.courseSystem.service.StudentTeacherService;




/**
 * 
 *
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@RestController
@RequestMapping("courseSystem/studentteacher")
public class StudentTeacherController {
    @Autowired
    private StudentTeacherService studentTeacherService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("courseSystem:studentteacher:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = studentTeacherService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("courseSystem:studentteacher:info")
    public R info(@PathVariable("id") Integer id){
		StudentTeacherEntity studentTeacher = studentTeacherService.getById(id);

        return R.ok().put("studentTeacher", studentTeacher);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("courseSystem:studentteacher:save")
    public R save(@RequestBody StudentTeacherEntity studentTeacher){
		studentTeacherService.save(studentTeacher);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("courseSystem:studentteacher:update")
    public R update(@RequestBody StudentTeacherEntity studentTeacher){
		studentTeacherService.updateById(studentTeacher);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("courseSystem:studentteacher:delete")
    public R delete(@RequestBody Integer[] ids){
		studentTeacherService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
