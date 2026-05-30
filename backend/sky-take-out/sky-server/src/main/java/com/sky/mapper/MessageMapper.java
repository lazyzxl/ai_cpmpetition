package com.sky.mapper;

import com.sky.entity.Message;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MessageMapper {
    
    @Insert("insert into message (user_id, competition_id, title, content, is_read, create_time) " +
            "values (#{userId}, #{competitionId}, #{title}, #{content}, #{isRead}, #{createTime})")
    void insert(Message message);
    
    @Select("select * from message where user_id = #{userId} order by create_time desc")
    java.util.List<Message> getByUserId(Long userId);
    
    @Update("update message set is_read = 1 where id = #{id}")
    void markAsRead(Long id);
    
    @Select("select count(*) from message where user_id = #{userId} and is_read = 0")
    Integer countUnread(Long userId);
    
    @Update("update message set is_read = 1 where user_id = #{userId}")
    void markAllRead(Long userId);
    
    @Select("select * from message order by create_time desc")
    java.util.List<Message> getAll();
    
    @Delete("delete from message where id = #{id}")
    void delete(Long id);
}