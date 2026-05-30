package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
    //关键词
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
