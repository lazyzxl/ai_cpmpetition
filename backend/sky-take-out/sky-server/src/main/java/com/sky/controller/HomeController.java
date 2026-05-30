package com.sky.controller;

import com.sky.context.BaseContext;
import com.sky.entity.Competition;
import com.sky.mapper.CareMapper;
import com.sky.mapper.CompetitionMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.CompetitionService;
import com.sky.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Api(tags = "首页相关接口")
@Slf4j
@RequestMapping("/home")
public class HomeController {
    
    @Autowired
    private HomeService homeService;
    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CareMapper careMapper;

    /**
     * 给比赛列表填充关注人数
     */
    private void fillCareCount(List<Competition> competitions) {
        if (competitions == null || competitions.isEmpty()) return;
        for (Competition c : competitions) {
            c.setCareCount(careMapper.countByCompetitionId(c.getId()));
        }
    }
    
    @ApiOperation("获取热门比赛推荐")
    @GetMapping("/hot-competitions")
    public Result<List<Competition>> getHotCompetitions(@RequestParam(defaultValue = "10") int limit) {
        log.info("获取热门比赛推荐，{}个", limit);
        List<Competition> competitions = homeService.getHotCompetitions(limit);
        fillCareCount(competitions);
        return Result.success(competitions);
    }
    
    @ApiOperation("获取猜你喜欢的比赛")
    @GetMapping("/recommended-competitions")
    public Result<List<Competition>> getRecommendedCompetitions(@RequestParam(defaultValue = "10") int limit) {
        Long userId = null;
        try {
            userId = BaseContext.getCurrentId();
        } catch (Exception e) {
            // 用户未登录，userId为null
        }
        log.info("获取推荐比赛，用户ID：{}，限制{}个", userId, limit);
        List<Competition> competitions = homeService.getRecommendedCompetitions(userId, limit);
        fillCareCount(competitions);
        return Result.success(competitions);
    }
    
    @ApiOperation("记录用户浏览比赛")
    @PostMapping("/record-view")
    public Result recordView(@RequestParam Long competitionId) {
        Long userId = null;
        try {
            userId = BaseContext.getCurrentId();
        } catch (Exception e) {
            // 用户未登录，userId为null
        }
        log.info("记录用户{}浏览比赛{}", userId, competitionId);
        homeService.recordUserView(userId, competitionId);
        return Result.success();
    }
    
    @ApiOperation("获取所有比赛")
    @GetMapping("/all-competitions")
    public Result<List<Competition>> getAllCompetitions() {
        log.info("获取所有比赛");
        List<Competition> competitions = competitionMapper.getAll();
        fillCareCount(competitions);
        return Result.success(competitions);
    }

    @ApiOperation("获取首页统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        log.info("获取首页统计数据");
        Map<String, Object> stats = new HashMap<>();

        // 用户总数
        stats.put("userCount", userMapper.count());

        // 比赛总数
        stats.put("competitionCount", competitionMapper.count());

        // 比赛类别数（不重复的title数量）
        List<Competition> competitions = competitionMapper.getAll();
        Set<String> categories = competitions.stream()
                .map(Competition::getTitle)
                .filter(title -> title != null && !title.isEmpty())
                .collect(Collectors.toSet());
        stats.put("categoryCount", categories.size());

        return Result.success(stats);
    }
}