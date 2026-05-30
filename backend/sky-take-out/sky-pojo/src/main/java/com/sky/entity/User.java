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
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;//主键
    private String username;//用户名
    private String password;//密码
    private String phone;
    private String profession;//专业
    private Integer status;//状态 0：锁定 1：正常
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
