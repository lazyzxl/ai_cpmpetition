package com.sky.mapper;

import com.sky.entity.AIAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AIAnswerMapper {
    
    @Select("SELECT * FROM ai_answer WHERE content_id = #{contentId} AND type = #{type} ORDER BY create_time DESC LIMIT 1")
    AIAnswer getLatestAnswer(Long contentId, String type);
    
    void insert(AIAnswer aiAnswer);
    
    @Select("SELECT * FROM ai_answer WHERE content_id = #{contentId} AND type = #{type} ORDER BY create_time DESC")
    List<AIAnswer> getAnswerHistory(Long contentId, String type);
}