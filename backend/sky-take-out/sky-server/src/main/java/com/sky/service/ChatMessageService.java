package com.sky.service;

import com.sky.entity.ChatMessage;
import com.sky.vo.ChatMessageVO;
import com.sky.vo.ChatSessionVO;

import java.util.List;

public interface ChatMessageService {

    /**
     * 发送消息
     */
    void sendMessage(Long senderId, Long receiverId, String content);

    /**
     * 获取聊天记录
     */
    List<ChatMessageVO> getChatHistory(Long userId1, Long userId2);

    /**
     * 获取聊天会话列表
     */
    List<ChatSessionVO> getChatSessions(Long userId);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long receiverId, Long senderId);

    /**
     * 获取未读消息数
     */
    int getUnreadCount(Long userId);
}
