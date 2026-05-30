package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Information implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//消息主键
    private Long competitionId;//相关比赛id
    private String content;//消息内容
    private LocalDateTime createTime;//发表时间
}
