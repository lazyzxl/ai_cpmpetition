package com.sky.service.impl;

import com.sky.dto.MessageDTO;
import com.sky.entity.Message;
import com.sky.mapper.CareMapper;
import com.sky.mapper.MessageMapper;
import com.sky.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private CareMapper careMapper;
    
    @Override
    public void send(MessageDTO messageDTO) {
        log.info("发送消息给用户{}", messageDTO.getUserId());
        
        Message message = new Message();
        message.setUserId(messageDTO.getUserId());
        message.setCompetitionId(messageDTO.getCompetitionId());
        message.setTitle(messageDTO.getTitle());
        message.setContent(messageDTO.getContent());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        log.info("消息发送成功");
    }
    
    @Override
    public void sendToCompetitionFollowers(MessageDTO messageDTO) {
        log.info("发送消息给关注比赛{}的用户", messageDTO.getCompetitionId());
        
        // 获取关注该比赛的所有用户ID
        List<Long> userIds = careMapper.getUserIdsByCompetitionId(messageDTO.getCompetitionId());
        
        if (userIds == null || userIds.isEmpty()) {
            log.info("没有用户关注该比赛");
            return;
        }
        
        // 为每个用户发送消息
        for (Long userId : userIds) {
            Message message = new Message();
            message.setUserId(userId);
            message.setCompetitionId(messageDTO.getCompetitionId());
            message.setTitle(messageDTO.getTitle());
            message.setContent(messageDTO.getContent());
            message.setIsRead(0);
            message.setCreateTime(LocalDateTime.now());
            
            messageMapper.insert(message);
        }
        
        log.info("成功发送消息给{}个用户", userIds.size());
    }
    
    @Override
    public List<Message> getMessagesByUserId(Long userId) {
        log.info("获取用户{}的消息列表", userId);
        return messageMapper.getByUserId(userId);
    }
    
    @Override
    public void markAsRead(Long messageId) {
        log.info("标记消息{}为已读", messageId);
        messageMapper.markAsRead(messageId);
    }
    
    @Override
    public Integer countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }
    
    @Override
    public void markAllRead(Long userId) {
        log.info("标记用户{}的所有消息为已读", userId);
        messageMapper.markAllRead(userId);
    }
    
    @Override
    public List<Message> getAllMessages() {
        log.info("获取所有消息");
        return messageMapper.getAll();
    }
    
    @Override
    public void deleteMessage(Long id) {
        log.info("删除消息：{}", id);
        messageMapper.delete(id);
    }
}