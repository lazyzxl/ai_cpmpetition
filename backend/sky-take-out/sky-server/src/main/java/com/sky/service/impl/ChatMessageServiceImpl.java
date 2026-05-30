package com.sky.service.impl;

import com.sky.entity.ChatMessage;
import com.sky.mapper.ChatMessageMapper;
import com.sky.service.ChatMessageService;
import com.sky.vo.ChatMessageVO;
import com.sky.vo.ChatSessionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public void sendMessage(Long senderId, Long receiverId, String content) {
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("不能给自己发消息");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("消息内容不能为空");
        }
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .isRead(0)
                .createTime(LocalDateTime.now())
                .build();
        chatMessageMapper.insert(message);
        log.info("用户{}发送消息给用户{}", senderId, receiverId);
    }

    @Override
    public List<ChatMessageVO> getChatHistory(Long userId1, Long userId2) {
        // 标记消息为已读
        chatMessageMapper.markAsRead(userId1, userId2);
        return chatMessageMapper.getChatHistory(userId1, userId2);
    }

    @Override
    public List<ChatSessionVO> getChatSessions(Long userId) {
        return chatMessageMapper.getChatSessions(userId);
    }

    @Override
    public void markAsRead(Long receiverId, Long senderId) {
        chatMessageMapper.markAsRead(receiverId, senderId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return chatMessageMapper.countUnread(userId);
    }
}
