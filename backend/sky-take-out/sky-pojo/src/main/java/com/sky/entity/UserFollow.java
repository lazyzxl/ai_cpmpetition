package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollow implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private Long userId;//关注者用户id
    private Long followUserId;//被关注者用户id
    private LocalDateTime createTime;//关注时间
}
