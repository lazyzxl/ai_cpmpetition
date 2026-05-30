package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.Message;
import com.sky.result.Result;
import com.sky.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "用户消息管理")
@Slf4j
@RequestMapping("/user/message")
public class userMessageController {
    @Autowired
    private MessageService messageService;
    @ApiOperation("获取我的消息列表")
    @GetMapping("/list")
    public Result<List<Message>> getMyMessages() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的消息列表", userId);
        List<Message> messages = messageService.getMessagesByUserId(userId);
        return Result.success(messages);
    }
    @ApiOperation("标记消息为已读")
    @PutMapping("/markAsRead")
    public Result markAsRead(@RequestParam Long messageId) {
        log.info("标记消息{}为已读", messageId);
        messageService.markAsRead(messageId);
        return Result.success();
    }
    
    @ApiOperation("获取未读消息数量")
    @GetMapping("/unreadCount")
    public Result<Integer> getUnreadCount() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的未读消息数量", userId);
        Integer count = messageService.countUnread(userId);
        return Result.success(count);
    }
    
    @ApiOperation("标记所有消息为已读")
    @PutMapping("/markAllRead")
    public Result markAllRead() {
        Long userId = BaseContext.getCurrentId();
        log.info("标记用户{}的所有消息为已读", userId);
        messageService.markAllRead(userId);
        return Result.success();
    }
}