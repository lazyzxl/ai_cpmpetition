package com.sky.vo;

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
public class CareItemVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;                    // 关注记录ID
    private Long competitionId;         // 比赛ID
    private String competitionName;     // 比赛名称
    private String deadline;            // 截止时间
    private String status;              // 状态
    private LocalDateTime createTime;   // 关注时间
}