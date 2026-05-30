package com.sky.controller.admin;

import com.sky.entity.Competition;
import com.sky.entity.Report;
import com.sky.mapper.CareMapper;
import com.sky.mapper.CompetitionMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.mapper.postMapper;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/admin/statistics")
@Api(tags = "统计数据接口")
@Slf4j
public class StatisticsController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private postMapper postMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private CareMapper careMapper;

    @ApiOperation("获取数据概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        log.info("获取数据概览");
        Map<String, Object> stats = new HashMap<>();

        Map<String, Integer> basicStats = new HashMap<>();
        basicStats.put("userCount", userMapper.count());
        basicStats.put("competitionCount", competitionMapper.count());
        basicStats.put("postCount", postMapper.count());
        basicStats.put("reportCount", reportMapper.getCount());
        stats.put("basicStats", basicStats);

        Map<String, Integer> careStats = new HashMap<>();
        List<Competition> competitions = competitionMapper.getLatestCompetitions(100);
        int totalCares = 0;
        for (Competition c : competitions) {
            totalCares += careMapper.countByCompetitionId(c.getId());
        }
        careStats.put("totalCares", totalCares);
        careStats.put("competitionWithCares", (int) competitions.stream().filter(c -> careMapper.countByCompetitionId(c.getId()) > 0).count());
        stats.put("careStats", careStats);

        return Result.success(stats);
    }

    @ApiOperation("获取比赛排行榜")
    @GetMapping("/competitionRanking")
    public Result<List<Map<String, Object>>> getCompetitionRanking() {
        log.info("获取比赛排行榜");
        List<Competition> competitions = competitionMapper.getLatestCompetitions(100);

        List<Map<String, Object>> ranking = new ArrayList<>();
        for (Competition c : competitions) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("name", c.getName());
            item.put("careCount", careMapper.countByCompetitionId(c.getId()));
            item.put("createTime", c.getCreateTime());
            ranking.add(item);
        }

        ranking.sort((a, b) -> {
            Integer careA = (Integer) a.get("careCount");
            Integer careB = (Integer) b.get("careCount");
            return careB.compareTo(careA);
        });

        if (ranking.size() > 10) {
            ranking = ranking.subList(0, 10);
        }

        return Result.success(ranking);
    }

    @ApiOperation("获取用户活跃统计")
    @GetMapping("/userActivity")
    public Result<Map<String, Object>> getUserActivity() {
        log.info("获取用户活跃统计");
        Map<String, Object> activity = new HashMap<>();

        Integer totalUsers = userMapper.count();
        List<Competition> competitions = competitionMapper.getLatestCompetitions(1000);
        Set<Long> activeUserIds = new HashSet<>();
        for (Competition c : competitions) {
            List<Long> userIds = careMapper.getUserIdsByCompetitionId(c.getId());
            activeUserIds.addAll(userIds);
        }

        activity.put("totalUsers", totalUsers);
        activity.put("activeUsers", activeUserIds.size());
        activity.put("inactiveUsers", totalUsers - activeUserIds.size());
        activity.put("activeRate", totalUsers > 0 ? (activeUserIds.size() * 100.0 / totalUsers) : 0);

        return Result.success(activity);
    }

    @ApiOperation("获取举报统计")
    @GetMapping("/reportStatistics")
    public Result<Map<String, Object>> getReportStatistics() {
        log.info("获取举报统计");
        Map<String, Object> reportStats = new HashMap<>();

        Integer pendingReports = reportMapper.getCount();
        List<Report> allReports = reportMapper.getAll();

        int processedReports = 0;
        int rejectedReports = 0;
        for (Report r : allReports) {
            if (r.getStatus() != null) {
                if (r.getStatus() == 1) {
                    processedReports++;
                } else if (r.getStatus() == 2) {
                    rejectedReports++;
                }
            }
        }

        reportStats.put("pendingReports", pendingReports);
        reportStats.put("processedReports", processedReports);
        reportStats.put("rejectedReports", rejectedReports);
        reportStats.put("totalReports", allReports.size());

        return Result.success(reportStats);
    }

    @ApiOperation("获取数据总览")
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary() {
        log.info("获取数据总览");
        Map<String, Object> summary = new HashMap<>();

        summary.put("userCount", userMapper.count());
        summary.put("competitionCount", competitionMapper.count());
        summary.put("postCount", postMapper.count());

        List<Competition> competitions = competitionMapper.getLatestCompetitions(1000);
        int totalCares = 0;
        for (Competition c : competitions) {
            totalCares += careMapper.countByCompetitionId(c.getId());
        }
        summary.put("totalCares", totalCares);

        summary.put("reportCount", reportMapper.getCount());

        return Result.success(summary);
    }
}
