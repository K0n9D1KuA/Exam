package com.apxy.courseSystem.controller;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.entity.DoneSubject;
import com.apxy.courseSystem.entity.vo.DoneSubjectVo;
import com.apxy.courseSystem.service.DoneSubjectService;
import com.apxy.courseSystem.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseSystem/doneSubject")
public class DoneSubjectController {

    @Autowired
    private DoneSubjectService doneSubjectService;

    /**
     * 保存修改后的大题 以及更新试卷状态和试卷分数
     *
     * @param doneSubjectList 需要修改的大题
     * @return 无
     */
    @PreAuthorize("hasAuthority('/correctingPaper')")
    @PostMapping("/correctPaper/{paperId}")
    public R correctPaper(@RequestBody String doneSubjectList, @PathVariable Long paperId) {

        List<DoneSubjectVo> doneSubjectVos = JSON.parseArray(JSON.parseObject(doneSubjectList).getString("doneSubjectList"), DoneSubjectVo.class);
        doneSubjectService.CompleteTheExaminationPaperCorrection(paperId, doneSubjectVos);
        return R.ok();
    }

    /**
     * 获得当前用户的错题
     */


    @GetMapping("/getDoneSubjects")
    public R getDoneSubjects() {
        List<DoneSubject> ret = doneSubjectService.getAllDoneWrongSubjects();
        return R.ok().put("data", ret);
    }

    /**
     * 组成一套模拟卷
     */



}
