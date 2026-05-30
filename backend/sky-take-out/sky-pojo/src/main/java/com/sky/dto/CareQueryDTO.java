package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //当前用户id
    private Long userId;
    //比赛名称
    private String name;
    //页码
    private int page;
    //每页记录数
    private int pageSize;
}
