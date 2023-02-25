package com.apxy.courseSystem.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.apxy.courseSystem.entity.*;
import com.apxy.courseSystem.entity.vo.*;
import com.apxy.courseSystem.service.DonePaperService;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import com.apxy.courseSystem.service.PaperService;


/**
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@RestController
@RequestMapping("courseSystem/paper")
public class PaperController {
    @Autowired
    private PaperService paperService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private DonePaperService donePaperService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 列表
     */

    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/list")
    //@RequiresPermissions("courseSystem:paper:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = paperService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获得所有的试卷
     */

    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/allPapers")
    public R allList() {

        return paperService.getAllPapers();
    }


    /**
     * 信息
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("courseSystem:paper:info")
    public R info(@PathVariable("id") Integer id) {
        PaperEntity paper = paperService.getById(id);

        return R.ok().put("paper", paper);
    }

    /**
     * 保存
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/save")
    // @RequiresPermissions("courseSystem:paper:save")
    public R save(@RequestBody PaperEntity paper) {
        paperService.save(paper);

        return R.ok();
    }

    /**
     * 修改
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/update")
    // @RequiresPermissions("courseSystem:paper:update")
    public R update(@RequestBody PaperEntity paper) {
        paperService.updateById(paper);

        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @RequestMapping("/delete")
    // @RequiresPermissions("courseSystem:paper:delete")
    public R delete(@RequestBody Integer[] ids) {
        paperService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 添加试卷
     */
    //
    @PreAuthorize("hasAuthority('/addPaper')")
    @PostMapping("/addPaper")
    public R addPaper(@RequestBody PaperVoEntity paperVoEntity) {
        //添加试卷
        paperService.addPaper(paperVoEntity);
        return R.ok();
    }

    /**
     * 点击开始答题  将试卷存到redis中 返回试卷中的所有题目数
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @GetMapping("/doPaper")
    public R doPaper(Long id) {
        return R.ok().put("data", paperService.doPaper(id));
    }

    /**
     * 提交题目并且获得下一题
     *
     * @param subjectVoEntity
     * @param index
     * @return
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @PostMapping("/getNextSubject/{index}")
    public R getNextSubject(@RequestBody SubjectVoEntity subjectVoEntity, @PathVariable Long index) {
        SubjectVoEntity ret = paperService.getNextSubject(subjectVoEntity, index);
        return R.ok().put("data", ret);
    }

    /**
     * 提交题目并且获得上一题
     *
     * @param subjectVoEntity
     * @param index
     * @return
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @PostMapping("/getLastSubject/{index}")
    public R getLastSubject(@RequestBody SubjectVoEntity subjectVoEntity, @PathVariable Long index) {
        SubjectVoEntity ret = paperService.getLastSubject(subjectVoEntity, index);
        return R.ok().put("data", ret);
    }

    @PreAuthorize("hasAuthority('/showPaper')")
    @PostMapping("/getOneSubject/{index}/{currentIndex}")
    public R getOneSubject(@PathVariable Long index, @RequestBody SubjectVoEntity subjectVoEntity, @PathVariable Long currentIndex) {
        SubjectVoEntity ret = paperService.getOneSubject(index, currentIndex, subjectVoEntity);
        return R.ok().put("data", ret);
    }

    /**
     * 获得当前试卷得分 不包含需要老师审批的大题
     */

    @PreAuthorize("hasAuthority('/showPaper')")
    @GetMapping("/getScoreRetWithNoShortAnswer/{teacherId}/{paperId}")
    public R getScoreRetWithNoShortAnswer(@PathVariable Long teacherId, @PathVariable Long paperId) {
        Map<String, Integer> map = paperService.getScoreRetWithNoShortAnswer(teacherId, paperId);
        return R.ok().put("data", map.get("score")).put("paperId", map.get("paperId"));
    }

    /**
     * 获得当前试卷得分
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @GetMapping("/getScoreRetWithShortAnswer")
    public R getScoreRetWithShortAnswer() {
        return R.ok().put("data", "需要交给老师审核哦！");
    }


    @PreAuthorize("hasAuthority('/showPaper')")
    @GetMapping("/getPaperSituation/{studentName}")
    public R getPaperSituation(@PathVariable String studentName) {
        return null;
    }


    /**
     * 修改某个索引下的题目
     */

    @PreAuthorize("hasAuthority('/showPaper')")
    @PostMapping("/changeSubjectByIndex/{index}")
    public R changeSubjectByIndex(@PathVariable Long index, @RequestBody SubjectVoEntity subjectVoEntity) {
        paperService.changeSubjectByIndex(index, subjectVoEntity);
        return R.ok().put("data", subjectVoEntity);
    }

    /**
     * 获得所有已经做完的试卷
     */

    @PreAuthorize("hasAuthority('/showPaper')")
    @GetMapping("/getAllDonePapers")
    public R getAllPapers(@RequestParam Map<String, Object> params) {
        PageUtils page = paperService.queryPageDonePaperPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 根据id获得试卷 并且获得该试卷下所有的题目
     *
     * @param id 试卷id
     * @return R
     */
    @PreAuthorize("hasAuthority('/showPaper')")
    @GetMapping("/getAllDonePapers/{id}")
    public R getDonePaper(@PathVariable Long id) {
        //获得改试卷下所有的题目
        List<DoneSubjectVo> retList = donePaperService.getAllDoneSubjectsByDonePaperId(id);
        //再获得试卷
        DonePaperEntity donePaperEntity = donePaperService.getById(id);
        DonePaperVo donePaperVo = new DonePaperVo();
        BeanUtils.copyProperties(donePaperEntity, donePaperVo);
        //获得学生姓名
        MemberEntity student = memberService.getById(donePaperEntity.getStudentId());
        donePaperVo.setStudentName(student.getMemberName());
        return R.ok().put("donePaper", donePaperVo).put("subjects", retList);
    }


    /**
     * 获取当前学生能做的试卷
     */
    @GetMapping("/getValidPaper")
    public R getValidPaper() {
        return paperService.getValidPaper();

    }


    /**
     * 获得全校所有试卷
     */
    @GetMapping("/getTotalSchoolPaper")
    public R getTotalSchoolPaper(@RequestParam Map<String, Object> params) {

        PageUtils ret = paperService.getTotalSchoolPaper(params);
        return R.ok().put("page", ret);

    }


}
