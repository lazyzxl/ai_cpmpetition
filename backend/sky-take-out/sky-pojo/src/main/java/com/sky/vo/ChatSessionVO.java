package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天会话VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;//聊天对象用户id
    private String username;//聊天对象用户名
    private String profession;//聊天对象专业
    private String lastMessage;//最后一条消息
    private Integer unreadCount;//未读消息数
    private LocalDateTime lastMessageTime;//最后消息时间
}
