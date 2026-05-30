package com.sky.mapper;

import com.sky.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface commentmapper {
    @Select("select * from comment where post_id=#{postId}")
    List<Comment> getByPostId(Long postId);
    @Insert("insert into comment (post_id,user_id,content,status,create_time)" +
            "values (#{postId},#{userId},#{content},#{status},#{createTime})")
    void save(Comment comment);
    @Update("update comment set status = #{status} where id = #{commentId}")
    void update(Long commentId);
    @Select("select * from comment where id = #{commentId}")
    Comment getById(Long commentId);

    @Delete("delete from comment where id = #{commentId}")
    void delete(Long commentId);
}
