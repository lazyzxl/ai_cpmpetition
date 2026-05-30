package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long postId;//帖子id
    private Long userId;//用户id
    private String content;//评论内容
    private LocalDateTime createTime;
    private Integer status;
    private String userName;//用户名（用于前端展示）
}
