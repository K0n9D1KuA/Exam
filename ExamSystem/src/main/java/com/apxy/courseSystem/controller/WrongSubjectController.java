package com.apxy.courseSystem.controller;


import com.apxy.courseSystem.entity.vo.WrongSubjectVo;
import com.apxy.courseSystem.service.WrongSubjectService;
import com.apxy.courseSystem.util.usingUtil.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courseSystem/wrongSubject")
public class WrongSubjectController {

    @Autowired
    private WrongSubjectService wrongSubjectService;

    //获得高频错题
    @GetMapping("/getAll")

    public R getAll() {
        List<WrongSubjectVo> retList = wrongSubjectService.getAll();
        return R.ok().put("data", retList);
    }
}
