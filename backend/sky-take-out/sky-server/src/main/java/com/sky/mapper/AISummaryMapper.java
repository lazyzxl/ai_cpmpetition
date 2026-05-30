package com.sky.mapper;

import com.sky.entity.AISummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AISummaryMapper {
    
    @Select("SELECT * FROM ai_summary WHERE resource_id = #{resourceId} AND type = #{type} ORDER BY create_time DESC LIMIT 1")
    AISummary getLatestSummary(Long resourceId, String type);
    
    void insert(AISummary aiSummary);
}