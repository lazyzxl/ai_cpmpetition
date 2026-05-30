package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private Long id;//主键
    private Long commentId;//评论id
    private String comment;//评论内容
    private LocalDateTime createTime;//举报时间
    private String cause;
    private Long userId;
    private long createUser;
    private Integer status;//0:待处理 1：已通过 2：已驳回
    private LocalDateTime updateTime;//处理时间
}
