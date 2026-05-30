package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnswer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private Long contentId;//关联的id
    private Long userId;//用户id
    private String question;//问题
    private String type;//类型
    private String answer;
    private LocalDateTime createTime;

}
