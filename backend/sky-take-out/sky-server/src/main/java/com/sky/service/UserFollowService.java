package com.sky.service;

import com.sky.entity.UserFollow;
import com.sky.vo.UserFollowVO;

import java.util.List;

public interface UserFollowService {

    /**
     * 关注用户
     */
    void follow(Long userId, Long followUserId);

    /**
     * 取消关注
     */
    void unfollow(Long userId, Long followUserId);

    /**
     * 检查是否已关注
     */
    boolean isFollowed(Long userId, Long followUserId);

    /**
     * 获取关注列表
     */
    List<UserFollowVO> getFollowList(Long userId);

    /**
     * 获取粉丝列表
     */
    List<UserFollowVO> getFansList(Long userId);

    /**
     * 获取粉丝数量
     */
    int getFansCount(Long userId);

    /**
     * 获取关注数量
     */
    int getFollowCount(Long userId);

    /**
     * 获取可关注的用户列表
     */
    List<UserFollowVO> getAllUsers(Long userId);
}
