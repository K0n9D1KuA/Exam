package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.DonePaperEntity;
import com.apxy.courseSystem.entity.DoneSubject;
import com.apxy.courseSystem.entity.vo.DonePaperVo;
import com.apxy.courseSystem.entity.vo.DoneSubjectVo;
import com.apxy.courseSystem.entity.vo.RankVo;
import com.apxy.courseSystem.util.R;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface DonePaperService extends IService<DonePaperEntity> {


    /**
     * 描述：根据已做试卷id 获得其所有的已做题目 需要处理一下题目学生上传图片
     *
     * @param donePaperId 试卷id
     * @return 该已做试卷下所有的已做题目
     */
    List<DoneSubjectVo> getAllDoneSubjectsByDonePaperId(Long donePaperId);

    /**
     * 描述：根据已做试卷id 获得 已做试卷 + 该试卷下所包含的所有题目
     *
     * @param donePaperId 已做试卷id
     * @return 已做试卷 + 该试卷下所包含的所有题目
     */
    R getDonePaperAndDoneSubjects(Long donePaperId);

    /**
     * 描述:获得该学生所有已做的试卷
     *
     * @return 该学生所有已做的试卷(含有出题老师姓名)
     */
    List<DonePaperVo> getDonePapers();

    /**
     * 根据已做过试卷id  查询该试卷下同班同学的排名情况
     *
     * @param donePaperId 试卷id
     * @return 排名情况
     */
    List<RankVo> getRankByPaperId(Long donePaperId);

    R getDonePaper();

    R getDonePaperDetail(Long donePaperId) throws ExecutionException, InterruptedException;
}
