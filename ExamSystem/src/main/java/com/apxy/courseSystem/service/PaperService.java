package com.apxy.courseSystem.service;

import com.apxy.courseSystem.entity.vo.PaperVoEntity;
import com.apxy.courseSystem.entity.vo.SubjectVoEntity;
import com.apxy.courseSystem.util.usingUtil.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.apxy.courseSystem.util.usingUtil.PageUtils;
import com.apxy.courseSystem.entity.PaperEntity;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
public interface PaperService extends IService<PaperEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addPaper(PaperVoEntity paperVoEntity);

   List<SubjectVoEntity> doPaper(Long id);

    SubjectVoEntity getNextSubject(SubjectVoEntity subjectVoEntity,Long index);

    SubjectVoEntity getLastSubject(SubjectVoEntity subjectVoEntity, Long index);

    SubjectVoEntity getOneSubject(Long index, Long currentIndex, SubjectVoEntity subjectVoEntity);

    Map<String, Integer> getScoreRetWithNoShortAnswer(Long teacherId, Long paperId);

    void changeSubjectByIndex(Long index, SubjectVoEntity subjectVoEntity);

    PageUtils queryPageDonePaperPage(Map<String, Object> params);

    R getValidPaper();

    R getAllPapers();

    PageUtils getTotalSchoolPaper(Map<String, Object> params);
}

