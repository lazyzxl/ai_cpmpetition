package com.sky.vo;

import com.sky.entity.Comment;
import com.sky.entity.Information;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO implements Serializable {
    private Long id;//帖子ID
    private Long userId;//作者用户ID
    private String title;//帖子标题
    private String content;//帖子内容
    private String authorName;//作者名称
    private LocalDateTime createTime;//发布时间
    private Integer viewCount;//浏览次数
    private Integer commentCount;//评论数
    private Integer likeCount;//点赞数
    private String tag;//帖子分类标签
    private List<Comment> comments;//评论列表
}
