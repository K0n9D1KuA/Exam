package com.apxy.courseSystem.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class ScoreAndPeopleCount implements Serializable {
    private static final long serialVersionUID = 1L;
    //分数
    private String score;
    //人数
    private Integer peopleCount;
}
