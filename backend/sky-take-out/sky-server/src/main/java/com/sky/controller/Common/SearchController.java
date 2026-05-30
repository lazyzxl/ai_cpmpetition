package com.sky.controller.Common;

import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.service.CompetitionService;
import com.sky.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/serch")
public class SearchController {
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private PostService postService;

    @GetMapping("/competition")
    @ApiOperation("关键词搜索比赛")
    public Result<PageResult> search(@RequestBody com.sky.dto.SearchDTO SearchDTO){
        log.info("搜索{}" ,SearchDTO);
        PageResult page=commonService.search(SearchDTO);
        return Result.success(page);
    }
    @ApiOperation("搜索帖子")
    @GetMapping("/post")
    public Result<PageResult> post(@RequestBody com.sky.dto.SearchDTO SearchDTO){
        log.info("搜索帖子{}" ,SearchDTO);
        PageResult page=postService.page(SearchDTO);
        return Result.success(page);
    }
}
