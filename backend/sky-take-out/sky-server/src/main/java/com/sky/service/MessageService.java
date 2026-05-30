package com.sky.service;

import com.sky.dto.MessageDTO;
import com.sky.entity.Message;

import java.util.List;

public interface MessageService {
    
    void send(MessageDTO messageDTO);
    
    void sendToCompetitionFollowers(MessageDTO messageDTO);
    
    List<Message> getMessagesByUserId(Long userId);
    
    void markAsRead(Long messageId);
    
    Integer countUnread(Long userId);
    
    void markAllRead(Long userId);
    
    List<Message> getAllMessages();
    
    void deleteMessage(Long id);
}