package com.sky.controller.admin;

import com.sky.dto.MessageDTO;
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
@Api(tags = "管理员消息管理")
@Slf4j
@RequestMapping("/admin/message")
public class adminMessageController {
    
    @Autowired
    private MessageService messageService;
    
    @ApiOperation("获取所有消息列表")
    @GetMapping("/selectPage")
    public Result<List<Message>> getMessagePage() {
        log.info("管理员获取消息列表");
        List<com.sky.entity.Message> messages = messageService.getAllMessages();
        return Result.success(messages);
    }
    
    @ApiOperation("删除消息")
    @DeleteMapping("/{id}")
    public Result deleteMessage(@PathVariable Long id) {
        log.info("管理员删除消息：{}", id);
        messageService.deleteMessage(id);
        return Result.success();
    }
    
    @ApiOperation("发布比赛消息并推送给关注用户")
    @PostMapping("/sendToCompetitionFollowers")
    public Result sendToCompetitionFollowers(@RequestBody MessageDTO messageDTO) {
        log.info("管理员发布比赛消息：{}", messageDTO);
        messageService.sendToCompetitionFollowers(messageDTO);
        return Result.success();
    }
}