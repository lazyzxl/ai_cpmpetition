package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;//用户id
    private Long competitionId;//比赛id
    private String title;//消息标题
    private String content;//消息内容
}