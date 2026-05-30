package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//关注记录id
    private Long followUserId;//被关注者用户id
    private String followUsername;//被关注者用户名
    private String followUserProfession;//被关注者专业
    private LocalDateTime createTime;//关注时间
}
