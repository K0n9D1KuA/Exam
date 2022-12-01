package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.DoneSubject;
import com.apxy.courseSystem.entity.vo.DoneSubjectVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DoneSubjectService extends IService<DoneSubject> {
    /**
     * 教师审核完大题 完成试卷的审核 1.更新做过题目表  2，修改试卷状态为已完成 3，更新试卷分数
     * @param donePaperId 批改试卷id
     * @param shortSubjectList 所有的大题列表
     */
    void CompleteTheExaminationPaperCorrection(Long donePaperId, List<DoneSubjectVo> shortSubjectList);

    /**
     * 获取该学生所有错题
     *
     * @return 该学生所有错题
     */
    List<DoneSubject> getAllDoneWrongSubjects();
}
