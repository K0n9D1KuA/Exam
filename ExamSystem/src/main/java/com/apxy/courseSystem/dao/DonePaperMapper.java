package com.apxy.courseSystem.dao;

import com.apxy.courseSystem.entity.DonePaperEntity;
import com.apxy.courseSystem.entity.ScoreAndPeopleCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface DonePaperMapper extends BaseMapper<DonePaperEntity> {


    List<ScoreAndPeopleCount> getDonePaperDetail(Long paperId);

}
