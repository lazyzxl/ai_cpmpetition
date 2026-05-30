package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SearchDTO;
import com.sky.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface postMapper {
    @Select("select * from post where competition_id=#{id} order by create_time desc")
    List<Post> getByCompetition(Long id);

    Post getById(Long postId);

    Page<Post> page(SearchDTO searchDTO);

    List<Post> list();

    void insert(Post post);

    @Select("select count(*) from post")
    Integer count();

    @Select("select * from post order by create_time desc limit #{limit}")
    List<Post> getLatestPosts(int limit);
}
