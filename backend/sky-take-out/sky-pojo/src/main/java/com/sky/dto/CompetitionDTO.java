package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompetitionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键（修改时需要）
    private String name;//比赛名称
    private String title;//比赛分类
    private String description;//描述
    private String url;//官网地址
    private String date;//举办时间
    private String organizer;//主办方
    private String location;//举办地点
    private Integer participantLimit;//参赛人数限制
    private String prize;//奖项设置
    private String contact;//联系方式
}