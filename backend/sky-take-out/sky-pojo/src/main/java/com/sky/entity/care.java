package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class care implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private Long competitionId;//比赛id
    private Long userId;//关注的用户id
    private LocalDateTime createTime;//关注时间
    private String url;//跳转链接
}
