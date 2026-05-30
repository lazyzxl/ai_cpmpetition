package com.sky.dto;
import lombok.Data;

@Data
public class aiDTO {
    // 资源类型：COMPETITION (比赛) 或 POST (帖子)
    private String resourceType;
    // 资源原始 ID (可能是重复的，比如都是 101)
    private Long resourceId;
    // 用户的问题 (仅提问接口需要，总结接口可为空)
    private String question;
}
