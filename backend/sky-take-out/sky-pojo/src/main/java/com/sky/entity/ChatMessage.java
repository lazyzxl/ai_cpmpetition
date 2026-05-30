package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private Long senderId;//发送者用户id
    private Long receiverId;//接收者用户id
    private String content;//消息内容
    private Integer isRead;//是否已读 0：未读 1：已读
    private LocalDateTime createTime;//创建时间
}
