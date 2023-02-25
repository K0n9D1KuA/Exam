package com.apxy.courseSystem.controller;


import com.apxy.courseSystem.entity.vo.DonePaperVo;
import com.apxy.courseSystem.entity.vo.RankVo;
import com.apxy.courseSystem.service.DonePaperService;
import com.apxy.courseSystem.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/courseSystem/donePaper")
public class DonePaperController {
    @Autowired
    private DonePaperService donePaperService;

    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/getDonePaper/{donePaperId}")
    public R getDonePaper(@PathVariable Long donePaperId) {
        return donePaperService.getDonePaperAndDoneSubjects(donePaperId);
    }

    /**
     * 查询学生做过的试卷
     */

    @GetMapping("/getDonePapers")
    public R getDonePapers() {
        List<DonePaperVo> retLits = donePaperService.getDonePapers();
        return R.ok().put("data", retLits);
    }

    /**
     * 根据试卷id  查询排名情况
     */

    @GetMapping("/getRank/{paperId}")
    public R getRank(@PathVariable Long paperId) {
        List<RankVo> ret = donePaperService.getRankByPaperId(paperId);
        return R.ok().put("data", ret).put("total", ret.size());
    }

//    /**
//     * 考试成绩分析
//     */
//    @GetMapping("/ScoreAnalysis")
//    public R getScoreAnalysis() {
//        return donePaperService.getScoreAnalysis();
//        return null;
//    }

    /**
     * 获得已做过试卷
     */
    @GetMapping("/getDonePaper")
    public R getDonePaper() {
        return donePaperService.getDonePaper();
    }

    /**
     * 根据试卷id 获得详细情况
     */
    @GetMapping("/getDonePaperDetail/{donePaperId}")
    public R getDonePaperDetail(@PathVariable Long donePaperId) throws ExecutionException, InterruptedException {
        //获得试卷详情
        return donePaperService.getDonePaperDetail(donePaperId);
    }


}
