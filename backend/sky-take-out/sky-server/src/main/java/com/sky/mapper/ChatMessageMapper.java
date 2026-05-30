package com.sky.mapper;

import com.sky.entity.ChatMessage;
import com.sky.vo.ChatMessageVO;
import com.sky.vo.ChatSessionVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    /**
     * 发送消息
     */
    @Insert("insert into chat_message (user_id, to_user_id, content, is_read, create_time) " +
            "values (#{senderId}, #{receiverId}, #{content}, #{isRead}, #{createTime})")
    void insert(ChatMessage chatMessage);

    /**
     * 获取与某个用户的所有聊天记录
     */
    @Select("select cm.id, cm.user_id as sender_id, cm.to_user_id as receiver_id, cm.content, cm.is_read, cm.create_time, " +
            "sender.username as sender_username, receiver.username as receiver_username " +
            "from chat_message cm " +
            "left join user sender on cm.user_id = sender.id " +
            "left join user receiver on cm.to_user_id = receiver.id " +
            "where (cm.user_id = #{userId1} and cm.to_user_id = #{userId2}) " +
            "or (cm.user_id = #{userId2} and cm.to_user_id = #{userId1}) " +
            "order by cm.create_time asc")
    List<ChatMessageVO> getChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 获取聊天会话列表
     */
    @Select("select * from (" +
            "select u.id as user_id, u.username, u.profession, " +
            "(select content from chat_message where " +
            "(user_id = #{userId} and to_user_id = u.id) or " +
            "(user_id = u.id and to_user_id = #{userId}) " +
            "order by create_time desc limit 1) as last_message, " +
            "(select count(*) from chat_message where user_id = u.id and to_user_id = #{userId} and is_read = 0) as unread_count, " +
            "(select create_time from chat_message where " +
            "(user_id = #{userId} and to_user_id = u.id) or " +
            "(user_id = u.id and to_user_id = #{userId}) " +
            "order by create_time desc limit 1) as last_message_time " +
            "from user u " +
            "where u.id != #{userId} and u.status = 1 and (" +
            "exists (select 1 from chat_message where user_id = #{userId} and to_user_id = u.id) or " +
            "exists (select 1 from chat_message where user_id = u.id and to_user_id = #{userId})" +
            ") " +
            "union all " +
            "select uf.follow_user_id as user_id, u.username, u.profession, " +
            "null as last_message, " +
            "0 as unread_count, " +
            "null as last_message_time " +
            "from user_follow uf " +
            "left join user u on uf.follow_user_id = u.id " +
            "where uf.user_id = #{userId} and uf.follow_user_id not in (" +
            "select case when user_id = #{userId} then to_user_id else user_id end " +
            "from chat_message where user_id = #{userId} or to_user_id = #{userId})" +
            ") t " +
            "order by last_message_time desc")
    List<ChatSessionVO> getChatSessions(Long userId);

    /**
     * 标记消息为已读
     */
    @Update("update chat_message set is_read = 1 where to_user_id = #{receiverId} and user_id = #{senderId} and is_read = 0")
    void markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    /**
     * 获取未读消息数
     */
    @Select("select count(*) from chat_message where to_user_id = #{userId} and is_read = 0")
    int countUnread(Long userId);
}
