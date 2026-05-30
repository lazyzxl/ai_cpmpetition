package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private Long userId;//接收消息的用户id
    private Long competitionId;//比赛id
    private String title;//消息标题
    private String content;//消息内容
    private Integer isRead;//是否已读 0：未读 1：已读
    private LocalDateTime createTime;//创建时间
}