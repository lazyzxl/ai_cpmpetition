package com.sky.service.impl;

import com.sky.entity.UserFollow;
import com.sky.mapper.UserFollowMapper;
import com.sky.service.UserFollowService;
import com.sky.vo.UserFollowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserFollowServiceImpl implements UserFollowService {

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public void follow(Long userId, Long followUserId) {
        // 不能关注自己
        if (userId.equals(followUserId)) {
            throw new RuntimeException("不能关注自己");
        }
        // 检查是否已关注
        if (userFollowMapper.countByUserIdAndFollowUserId(userId, followUserId) > 0) {
            throw new RuntimeException("已经关注过了");
        }
        UserFollow userFollow = UserFollow.builder()
                .userId(userId)
                .followUserId(followUserId)
                .createTime(LocalDateTime.now())
                .build();
        userFollowMapper.insert(userFollow);
        log.info("用户{}关注了用户{}", userId, followUserId);
    }

    @Override
    public void unfollow(Long userId, Long followUserId) {
        userFollowMapper.delete(userId, followUserId);
        log.info("用户{}取消关注了用户{}", userId, followUserId);
    }

    @Override
    public boolean isFollowed(Long userId, Long followUserId) {
        return userFollowMapper.countByUserIdAndFollowUserId(userId, followUserId) > 0;
    }

    @Override
    public List<UserFollowVO> getFollowList(Long userId) {
        return userFollowMapper.getFollowList(userId);
    }

    @Override
    public List<UserFollowVO> getFansList(Long userId) {
        return userFollowMapper.getFansList(userId);
    }

    @Override
    public int getFansCount(Long userId) {
        return userFollowMapper.countFans(userId);
    }

    @Override
    public int getFollowCount(Long userId) {
        return userFollowMapper.countFollow(userId);
    }

    @Override
    public List<UserFollowVO> getAllUsers(Long userId) {
        return userFollowMapper.getAllUsers(userId);
    }
}
