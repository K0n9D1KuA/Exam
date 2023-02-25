package com.apxy.courseSystem.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.enuem.LoginEnuem;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.util.usingUtil.Generate6RandomNumberUtil;
import com.apxy.courseSystem.util.usingUtil.PageUtils;
import com.apxy.courseSystem.util.usingUtil.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.apxy.courseSystem.entity.MemberEntity;


/**
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@RestController
@RequestMapping("courseSystem/student")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("courseSystem:student:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("courseSystem:student:info")
    public R info(@PathVariable("id") Integer id) {
        MemberEntity student = memberService.getById(id);

        return R.ok().put("student", student);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("courseSystem:student:save")
    public R save(@RequestBody MemberEntity student) {
        memberService.save(student);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("courseSystem:student:update")
    public R update(@RequestBody MemberEntity student) {
        memberService.updateById(student);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("courseSystem:student:delete")
    public R delete(@RequestBody Integer[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 发送验证码接口
     */
    /**
     * 根据邮箱发送验证码
     */
    @GetMapping("sendCode")
    //根据邮箱发送验证码
    public R sendCode(String email) {
        //首先校验验证码 是否存在
        LambdaQueryWrapper<MemberEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MemberEntity::getEmail, email);
        int count = memberService.count(lambdaQueryWrapper);
        if (count > 0) {
            //说明邮箱存在 返回邮箱已被别人注册过
            return R.error(LoginEnuem.EMAIL_EXIST_ERROR.getCode(), LoginEnuem.EMAIL_EXIST_ERROR.getMsg());
        }
        //随机生成验证码
        String randCode = Generate6RandomNumberUtil.randomCode();
        //邮件的标题
        String subject = AuthServerConstant.subject;
        //邮件的内容
        String context = AuthServerConstant.CONTEXT + randCode;
        memberService.sendCode(email, subject, context);
        //存入redis中 过期时间5min
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + email, JSON.toJSONString(randCode), 5, TimeUnit.MINUTES);
        //发送成功
        return R.ok().put("data", "发送验证码成功");

    }


//    @GetMapping("/test")
//    public R test() {
//        memberService.saveWithExcel();
//        return R.ok();
//    }

    /**
     * 查看我的学生
     *
     * @return
     */
    @PreAuthorize("hasAuthority('/list')")
    @GetMapping("/student/list")
    public R getStudentList(@RequestParam Map<String, Object> params) {

//        PageUtils page = memberService.queryPage(params);
//
//        return R.ok().put("page", page);

        PageUtils page = memberService.queryAllStudents(params);
        return R.ok().put("page", page);
    }

    /**
     * 获得全校学生列表 需要展示老师是谁
     * @param params
     * @return
     */
    @PreAuthorize("hasAuthority('/allList')")
    @GetMapping("/studentWithTeacher/list")
    public R getStudentListWithTeacher(@RequestParam Map<String, Object> params) {

//        PageUtils page = memberService.queryPage(params);
//
//        return R.ok().put("page", page);

        PageUtils page = memberService.queryAllStudentsWithTeacher(params);
        return R.ok().put("page", page);
    }

    /**
     * 获得全校老师
     */
    @GetMapping("/allTeacher/list")
    public R getAllTeacher(@RequestParam Map<String, Object> params) {

//        PageUtils page = memberService.queryPage(params);
//
//        return R.ok().put("page", page);

        PageUtils page = memberService.getAllTeacher(params);

        return R.ok().put("page", page);
    }
}
