package com.sky.dto;

import lombok.Data;

@Data
public class ReportPage {
    Integer page;//页码
    Integer pageSize;//每页的数量
    Integer status;//状态 0：待处理，1：已通过，2：已驳回，为null时查询所有
    String startTime;//开始时间
    String endTime;//结束时间
}
