package com.sky.service;

import com.sky.dto.PostDTO;
import com.sky.dto.SearchDTO;
import com.sky.entity.Post;
import com.sky.result.PageResult;
import com.sky.vo.PostVO;

import java.util.List;

public interface PostService {
    PostVO getById(Long postId);

    void saveComment(Long postId, String content);

    PageResult page(SearchDTO searchDTO);

    List<PostVO> list(Integer page, Integer pageSize);

    void save(PostDTO postDTO);
}
