package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.PostDTO;
import com.sky.dto.SearchDTO;
import com.sky.entity.Comment;
import com.sky.entity.Post;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.mapper.commentmapper;
import com.sky.mapper.postMapper;
import com.sky.result.PageResult;
import com.sky.service.PostService;
import com.sky.vo.PostVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private postMapper postMapper;
    @Autowired
    private commentmapper commentmapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public PostVO getById(Long postId) {
        PostVO postVO = new PostVO();
        Post post = postMapper.getById(postId);
        BeanUtils.copyProperties(post, postVO);
        
        // 获取作者名称
        if (post.getUserId() != null) {
            User user = userMapper.getById(post.getUserId());
            if (user != null) {
                postVO.setAuthorName(user.getUsername());
            }
        }
        
        // 获取评论并设置评论作者名称
        List<Comment> comments = commentmapper.getByPostId(postId);
        if (comments != null) {
            for (Comment comment : comments) {
                if (comment.getUserId() != null) {
                    User user = userMapper.getById(comment.getUserId());
                    if (user != null) {
                        comment.setUserName(user.getUsername());
                    }
                }
            }
            postVO.setComments(comments);
            postVO.setCommentCount(comments.size());
        } else {
            postVO.setCommentCount(0);
        }
        
        // 设置默认浏览次数
        if (postVO.getViewCount() == null) {
            postVO.setViewCount(0);
        }
        // 设置默认点赞数
        if (postVO.getLikeCount() == null) {
            postVO.setLikeCount(0);
        }
        return postVO;
    }

    @Override
    public void saveComment(Long postId, String content) {
        Comment comment=new Comment();
        comment.setCreateTime(LocalDateTime.now());
        comment.setContent(content);
        comment.setUserId(BaseContext.getCurrentId());
        comment.setPostId(postId);
        comment.setStatus(StatusConstant.ENABLE);
        commentmapper.save(comment);
    }

    @Override
    public PageResult page(SearchDTO searchDTO) {
        PageHelper.startPage(searchDTO.getPage(),searchDTO.getPageSize());
        Page<Post> page= postMapper.page(searchDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<PostVO> list(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Post> posts = postMapper.list();
        
        // 将 Post 转换为 PostVO 并填充作者名称
        return posts.stream().map(post -> {
            PostVO postVO = new PostVO();
            BeanUtils.copyProperties(post, postVO);
            
            // 获取作者名称
            if (post.getUserId() != null) {
                User user = userMapper.getById(post.getUserId());
                if (user != null) {
                    postVO.setAuthorName(user.getUsername());
                }
            }
            
            // 获取评论数
            List<Comment> comments = commentmapper.getByPostId(post.getId());
            postVO.setCommentCount(comments != null ? comments.size() : 0);
            
            // 设置默认浏览次数和点赞数
            if (postVO.getViewCount() == null) {
                postVO.setViewCount(0);
            }
            if (postVO.getLikeCount() == null) {
                postVO.setLikeCount(0);
            }
            
            return postVO;
        }).toList();
    }

    @Override
    public void save(PostDTO postDTO) {
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post);
        post.setUserId(BaseContext.getCurrentId());
        post.setCreateTime(LocalDateTime.now());
        post.setStatus(StatusConstant.ENABLE);
        postMapper.insert(post);
    }
}
