package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Competition implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private String name;//比赛名称
    private String title;//比赛分类
    private String description;//描述
    private String url;//官网地址
    private String date;//举办时间
    private LocalDateTime updateTime;
    private String photourl;
    private LocalDateTime createTime;
    private String organizer;//主办方
    private String location;//举办地点
    private Integer participantLimit;//参赛人数限制
    private String prize;//奖项设置
    private String contact;//联系方式

    private Integer careCount;// 关注人数（非数据库字段）
}
