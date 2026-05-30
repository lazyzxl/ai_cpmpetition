package com.sky.controller.Post;

import com.sky.context.BaseContext;
import com.sky.dto.PostDTO;
import com.sky.dto.ReportDTO;
import com.sky.entity.Comment;
import com.sky.entity.Post;
import com.sky.mapper.commentmapper;
import com.sky.result.Result;
import com.sky.service.CommentService;
import com.sky.service.PostService;
import com.sky.service.ReportService;
import com.sky.vo.PostVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@Api(tags = "帖子详情")
public class PostController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private commentmapper commentmapper;

    @ApiOperation(value = "获取帖子列表")
    @GetMapping
    public Result<List<PostVO>> getPostList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "20") Integer pageSize){
        List<PostVO> posts = postService.list(page, pageSize);
        return Result.success(posts);
    }

    @ApiOperation(value = "发布帖子")
    @PostMapping
    public Result createPost(@RequestBody PostDTO postDTO){
        postService.save(postDTO);
        return Result.success();
    }

    @ApiOperation(value = "获取帖子里的内容")
    @GetMapping("{postId}")
    public Result<PostVO> getPostById(@PathVariable("postId") Long postId){
        PostVO postVO=postService.getById(postId);
        return Result.success(postVO);
    }

    @ApiOperation(value = "评论")
    @PostMapping("/{postId}")
    public Result saveComment(@PathVariable("postId") Long postId,String content){
        postService.saveComment(postId,content);
        return Result.success();
    }

    @ApiOperation(value = "举报评论")
    @PostMapping("/report")
    public Result updateComment(@RequestBody ReportDTO reportDTO){

        reportService.save(reportDTO, BaseContext.getCurrentId());
        return Result.success();
    }
}
