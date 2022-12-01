package com.apxy.courseSystem.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data

public class StaticVo implements Serializable {
    private static final long serialVersionUID = 1L;
    //分数
    List<String> scores = new ArrayList<>();
    //分数所对应的人数
    List<Integer> peopleCount = new ArrayList<>();
}
