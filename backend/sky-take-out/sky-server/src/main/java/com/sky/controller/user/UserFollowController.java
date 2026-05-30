package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.result.Result;
import com.sky.service.UserFollowService;
import com.sky.vo.UserFollowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "用户关注")
@Slf4j
@RequestMapping("/user/follow")
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    @PostMapping("/{followUserId}")
    @ApiOperation("关注用户")
    public Result follow(@PathVariable Long followUserId) {
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}关注用户{}", userId, followUserId);
        userFollowService.follow(userId, followUserId);
        return Result.success();
    }

    @DeleteMapping("/{followUserId}")
    @ApiOperation("取消关注")
    public Result unfollow(@PathVariable Long followUserId) {
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}取消关注用户{}", userId, followUserId);
        userFollowService.unfollow(userId, followUserId);
        return Result.success();
    }

    @GetMapping("/check/{followUserId}")
    @ApiOperation("检查是否已关注")
    public Result<Boolean> checkFollow(@PathVariable Long followUserId) {
        Long userId = BaseContext.getCurrentId();
        boolean followed = userFollowService.isFollowed(userId, followUserId);
        return Result.success(followed);
    }

    @GetMapping("/list")
    @ApiOperation("获取关注列表")
    public Result<List<UserFollowVO>> getFollowList() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的关注列表", userId);
        List<UserFollowVO> list = userFollowService.getFollowList(userId);
        return Result.success(list);
    }

    @GetMapping("/fans")
    @ApiOperation("获取粉丝列表")
    public Result<List<UserFollowVO>> getFansList() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的粉丝列表", userId);
        List<UserFollowVO> list = userFollowService.getFansList(userId);
        return Result.success(list);
    }

    @GetMapping("/fans/count")
    @ApiOperation("获取粉丝数量")
    public Result<Integer> getFansCount() {
        Long userId = BaseContext.getCurrentId();
        int count = userFollowService.getFansCount(userId);
        return Result.success(count);
    }

    @GetMapping("/follow/count")
    @ApiOperation("获取关注数量")
    public Result<Integer> getFollowCount() {
        Long userId = BaseContext.getCurrentId();
        int count = userFollowService.getFollowCount(userId);
        return Result.success(count);
    }

    @GetMapping("/users")
    @ApiOperation("获取可关注的用户列表")
    public Result<List<UserFollowVO>> getAllUsers() {
        Long userId = BaseContext.getCurrentId();
        List<UserFollowVO> list = userFollowService.getAllUsers(userId);
        return Result.success(list);
    }
}
