package com.sky.config;

import com.sky.entity.Competition;
import com.sky.mapper.CompetitionMapper;
import com.sky.service.impl.HomeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConfig {
    
    @Autowired
    private CompetitionMapper competitionMapper;
    
    @Autowired
    private HomeServiceImpl homeService;
    
    /**
     * 每天凌晨1点更新所有比赛的热度分数
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateAllCompetitionsHotScore() {
        log.info("开始更新所有比赛的热度分数");
        try {
            // 获取所有比赛
            List<Competition> competitions = competitionMapper.getLatestCompetitions(1000); // 假设最多1000个比赛
            if (competitions != null && !competitions.isEmpty()) {
                for (Competition competition : competitions) {
                    // 触发热度分数更新
                    homeService.recordUserView(null, competition.getId());
                }
                log.info("成功更新{}个比赛的热度分数", competitions.size());
            }
        } catch (Exception e) {
            log.error("更新比赛热度分数失败", e);
        }
    }
}