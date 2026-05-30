package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long id;//主键
    private Long competitionId;//比赛id
    private LocalDateTime createTime;//创建时间
    private String title;//帖子标题
    private  String content;//帖子内容
    private String url;//在比赛详细页面通过点击帖子跳转到帖子详细页面
    private int status;
    private String tag;//帖子分类标签
}
