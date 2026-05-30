package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.result.Result;
import com.sky.service.ChatMessageService;
import com.sky.vo.ChatMessageVO;
import com.sky.vo.ChatSessionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "用户聊天")
@Slf4j
@RequestMapping("/user/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @PostMapping("/send")
    @ApiOperation("发送消息")
    public Result sendMessage(@RequestParam Long receiverId, @RequestParam String content) {
        Long senderId = BaseContext.getCurrentId();
        log.info("用户{}发送消息给用户{}", senderId, receiverId);
        chatMessageService.sendMessage(senderId, receiverId, content);
        return Result.success();
    }

    @GetMapping("/history/{userId}")
    @ApiOperation("获取聊天记录")
    public Result<List<ChatMessageVO>> getChatHistory(@PathVariable Long userId) {
        Long currentUserId = BaseContext.getCurrentId();
        log.info("获取用户{}和用户{}的聊天记录", currentUserId, userId);
        List<ChatMessageVO> list = chatMessageService.getChatHistory(currentUserId, userId);
        return Result.success(list);
    }

    @GetMapping("/sessions")
    @ApiOperation("获取聊天会话列表")
    public Result<List<ChatSessionVO>> getChatSessions() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的聊天会话列表", userId);
        List<ChatSessionVO> list = chatMessageService.getChatSessions(userId);
        return Result.success(list);
    }

    @PutMapping("/read/{senderId}")
    @ApiOperation("标记消息为已读")
    public Result markAsRead(@PathVariable Long senderId) {
        Long receiverId = BaseContext.getCurrentId();
        log.info("用户{}标记来自用户{}的消息为已读", receiverId, senderId);
        chatMessageService.markAsRead(receiverId, senderId);
        return Result.success();
    }

    @GetMapping("/unread")
    @ApiOperation("获取未读消息数")
    public Result<Integer> getUnreadCount() {
        Long userId = BaseContext.getCurrentId();
        int count = chatMessageService.getUnreadCount(userId);
        return Result.success(count);
    }
}
