package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AISummary {
    private Long id;//主键
    private Long resourceId;//来源
    private String type;//类型
    private LocalDateTime createTime;//创建时间
    private String content;//ai的生成总结
}
