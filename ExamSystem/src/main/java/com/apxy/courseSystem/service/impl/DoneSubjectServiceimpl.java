package com.apxy.courseSystem.service.impl;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.constant.PaperConstant;
import com.apxy.courseSystem.constant.SubjectConstant;
import com.apxy.courseSystem.dao.DoneSubjectDao;
import com.apxy.courseSystem.entity.DonePaperEntity;
import com.apxy.courseSystem.entity.DoneSubject;
import com.apxy.courseSystem.entity.event.DoneSubjectImageEvent;
import com.apxy.courseSystem.entity.event.MemberEvent;
import com.apxy.courseSystem.entity.vo.DoneSubjectVo;
import com.apxy.courseSystem.entity.vo.SubjectVoEntity;
import com.apxy.courseSystem.service.DonePaperService;
import com.apxy.courseSystem.service.DoneSubjectService;
import com.apxy.courseSystem.util.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DoneSubjectServiceimpl extends ServiceImpl<DoneSubjectDao, DoneSubject> implements DoneSubjectService {
    @Autowired
    private DonePaperService donePaperService;
    @Autowired
    private SpringSecurityUtil springSecurityUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 教师审核完大题 完成试卷的审核 1.更新做过题目表  2，修改试卷状态为已完成 3，更新试卷分数
     *
     * @param donePaperId      批改试卷id
     * @param shortSubjectList 所有的大题列表
     */
    @Override
    public void CompleteTheExaminationPaperCorrection(Long donePaperId, List<DoneSubjectVo> shortSubjectList) {
        List<DoneSubject> collect = shortSubjectList.stream().map(o -> {
            DoneSubject doneSubject = new DoneSubject();
            BeanUtils.copyProperties(o, doneSubject);
            return doneSubject;
        }).collect(Collectors.toList());
        //批量更新题目
        this.updateBatchById(collect);
        //更新试卷
        DonePaperEntity donePaperEntity = donePaperService.getById(donePaperId);
        //修改试卷的状态
        donePaperEntity.setPaperType(PaperConstant.COMPLETED);
        //获得大题的分数
        Integer score = 0;
        for (DoneSubjectVo o : shortSubjectList
        ) {
            score += o.getActualScore();
        }
        donePaperEntity.setActualScore(donePaperEntity.getActualScore() + score);
        donePaperService.updateById(donePaperEntity);
    }

    /**
     * 获取该学生所有错题
     *
     * @return 该学生所有错题
     */
    @Override
    public List<DoneSubject> getAllDoneWrongSubjects() {
        //获得该学生所有做过的试卷
        List<DonePaperEntity> list = donePaperService.list(new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getStudentId, this.getId()));
        //健壮性判断
        if (list == null || list.size() == 0) {
            //说明该学生没有错题
            return null;
        } else {
            //收集做过试卷id
            List<Integer> donePaperIds = list.stream().map(DonePaperEntity::getId).collect(Collectors.toList());
            //收集所有题目 需要过滤掉正确的题目 正确的题目就是实际得分等于题目总分
            return this.list(new LambdaQueryWrapper<DoneSubject>().in(DoneSubject::getDonePaperId, donePaperIds)).stream().filter(o -> {
                return (o.getActualScore() != o.getScore().intValue());
            }).collect(Collectors.toList());
        }
    }

    /**
     * 获得当前学生的id
     */
    private Integer getId() {
        return springSecurityUtil.getUser().getMemberEntity().getId();
    }


    /**
     * 监听学生上传题目答案图片业务 从redis中修改
     * 图片的存储格式  http:dsjaiojdsaijdfjsafa;http:dsjaiojdsaijdfjsafa;
     * @param
     */
    @Async
    @EventListener(DoneSubjectImageEvent.class)
    public void doEvent(DoneSubjectImageEvent
                                doneSubjectImageEvent) {
        System.out.println("上传图片副线程开始...................");
        //题目图片
        String imageUrl = doneSubjectImageEvent.getImageUrl();
        //上传学生姓名
        String memberName = doneSubjectImageEvent.getMemberName();
        //redis key
        String key = AuthServerConstant.PAPER_PREFIX_KEY + memberName;
        //上传图片大题的索引
        Long currentIndex = doneSubjectImageEvent.getCurrentIndex();

        String jsonString = stringRedisTemplate.opsForList().index(key, currentIndex - 1);
        SubjectVoEntity subjectVoEntity = JSON.parseObject(jsonString, SubjectVoEntity.class);
        subjectVoEntity.setSelectAnswer(subjectVoEntity.getSelectAnswer() + imageUrl + ";");
        String s = JSON.toJSONString(subjectVoEntity);
        stringRedisTemplate.opsForList().set(key, currentIndex - 1, s);
        System.out.println("上传图片副线程结束...................");
        // http:wwww;djiajsd;djsaidjsa;
    }

}
