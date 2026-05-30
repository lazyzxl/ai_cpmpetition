package com.sky.service.impl;

import com.sky.entity.Competition;
import com.sky.entity.User;
import com.sky.mapper.CareMapper;
import com.sky.mapper.CompetitionMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HomeServiceImpl implements HomeService {
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private CompetitionMapper competitionMapper;
    
    @Autowired
    private CareMapper careMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    private static final String HOT_COMPETITIONS_KEY = "hot_competitions";
    private static final String USER_VIEW_KEY = "user_view:";
    private static final String COMPETITION_VIEW_KEY = "competition_view:";
    
    @Override
    public List<Competition> getHotCompetitions(int limit) {
        log.info("获取热门比赛，限制{}个", limit);
        try {
            // 从Redis中获取热门比赛ID
            Set<String> competitionIds = redisTemplate.opsForZSet().reverseRange(HOT_COMPETITIONS_KEY, 0, limit - 1);
            if (competitionIds != null && !competitionIds.isEmpty()) {
                // 根据ID查询比赛详情
                List<Long> ids = competitionIds.stream().map(Long::parseLong).collect(Collectors.toList());
                return getCompetitionsByIds(ids);
            }
        } catch (Exception e) {
            log.warn("Redis操作失败，使用数据库数据", e);
        }
        // 如果Redis中没有数据或操作失败，从数据库中获取最新比赛
        return getLatestCompetitions(limit);
    }
    
    /**
     * 计算比赛热度分数
     * 综合考虑：浏览量、关注量、创建时间
     */
    private double calculateHotScore(Long competitionId, int viewCount, int careCount, long createTime) {
        // 时间衰减因子（每天衰减10%）
        long now = System.currentTimeMillis();
        long daysSinceCreation = (now - createTime) / (1000 * 60 * 60 * 24);
        double timeDecay = Math.pow(0.9, daysSinceCreation);
        
        // 加权计算
        double viewWeight = 0.5;      // 浏览量权重
        double careWeight = 0.3;      // 关注量权重
        double timeWeight = 0.2;      // 时间权重
        
        // 基础分数
        double baseScore = viewCount * viewWeight + careCount * careWeight * 5; // 关注权重更高
        
        // 时间因子（越新的比赛分数越高）
        double timeScore = 100.0 / (1 + daysSinceCreation);
        
        // 综合分数
        return (baseScore + timeScore * timeWeight) * timeDecay;
    }
    
    @Override
    public List<Competition> getRecommendedCompetitions(Long userId, int limit) {
        log.info("获取用户{}的推荐比赛，限制{}个", userId, limit);
        
        if (userId == null) {
            // 未登录用户，返回热门比赛
            return getHotCompetitions(limit);
        }
        
        try {
            // 1. 基于用户浏览历史推荐
            Set<String> viewedCompetitionIds = redisTemplate.opsForSet().members(USER_VIEW_KEY + userId);
            if (viewedCompetitionIds != null && !viewedCompetitionIds.isEmpty()) {
                // 基于浏览历史推荐相似比赛
                return getRecommendationsBasedOnViewHistory(userId, limit);
            }
        } catch (Exception e) {
            log.warn("Redis操作失败，跳过浏览历史推荐", e);
        }
        
        // 2. 基于用户专业推荐
        List<Competition> professionalRecommendations = getRecommendationsBasedOnProfession(userId, limit);
        if (professionalRecommendations != null && !professionalRecommendations.isEmpty()) {
            return professionalRecommendations;
        }
        
        // 3. 基于热门比赛推荐
        return getHotCompetitions(limit);
    }
    
    /**
     * 基于用户浏览历史推荐比赛
     */
    private List<Competition> getRecommendationsBasedOnViewHistory(Long userId, int limit) {
        // 这里可以实现更复杂的基于浏览历史的推荐算法
        // 暂时返回热门比赛
        return getHotCompetitions(limit);
    }
    
    /**
     * 基于用户专业推荐比赛
     */
    private List<Competition> getRecommendationsBasedOnProfession(Long userId, int limit) {
        try {
            // 获取用户信息
            User user = getUserById(userId);
            if (user != null && user.getProfession() != null && !user.getProfession().isEmpty()) {
                String profession = user.getProfession();
                log.info("基于用户专业{}推荐比赛", profession);
                
                // 基于专业关键词匹配比赛标题或描述
                // 这里简化处理，实际可以使用更复杂的匹配算法
                List<Competition> competitions = competitionMapper.getLatestCompetitions(50);
                if (competitions != null && !competitions.isEmpty()) {
                    // 过滤出与专业相关的比赛
                    List<Competition> filteredCompetitions = competitions.stream()
                            .filter(competition -> {
                                String title = competition.getTitle() != null ? competition.getTitle() : "";
                                String description = competition.getDescription() != null ? competition.getDescription() : "";
                                return title.contains(profession) || description.contains(profession);
                            })
                            .limit(limit)
                            .collect(java.util.stream.Collectors.toList());
                    
                    if (!filteredCompetitions.isEmpty()) {
                        return filteredCompetitions;
                    }
                }
            }
        } catch (Exception e) {
            log.error("基于专业推荐比赛失败", e);
        }
        return null;
    }
    
    /**
     * 根据用户ID获取用户信息
     */
    private User getUserById(Long userId) {
        return userMapper.getById(userId);
    }
    @Override
    public void recordUserView(Long userId, Long competitionId) {
        log.info("记录用户{}浏览比赛{}", userId, competitionId);
        
        // 记录用户浏览历史
        if (userId != null) {
            redisTemplate.opsForSet().add(USER_VIEW_KEY + userId, competitionId.toString());
            // 设置过期时间（30天）
            redisTemplate.expire(USER_VIEW_KEY + userId, 30 * 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
        }
        
        // 增加比赛浏览次数
        redisTemplate.opsForValue().increment(COMPETITION_VIEW_KEY + competitionId);
        
        // 重新计算热度分数
        updateCompetitionHotScore(competitionId);
    }
    
    /**
     * 更新比赛的热度分数
     */
    private void updateCompetitionHotScore(Long competitionId) {
        try {
            // 获取比赛信息
            Competition competition = competitionMapper.getById(competitionId);
            if (competition == null) {
                return;
            }
            
            // 获取浏览量
            Integer viewCount = 0;
            Object viewCountObj = redisTemplate.opsForValue().get(COMPETITION_VIEW_KEY + competitionId);
            if (viewCountObj != null) {
                viewCount = Integer.parseInt(viewCountObj.toString());
            }
            
            // 获取关注量
            int careCount = getCompetitionCareCount(competitionId);
            
            // 计算热度分数
            long createTime = competition.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            double hotScore = calculateHotScore(competitionId, viewCount, careCount, createTime);
            
            // 更新热度分数
            redisTemplate.opsForZSet().add(HOT_COMPETITIONS_KEY, competitionId.toString(), hotScore);
            
            log.info("更新比赛{}的热度分数：{}", competitionId, hotScore);
        } catch (Exception e) {
            log.error("更新比赛热度分数失败", e);
        }
    }
    
    /**
     * 获取比赛的关注量
     */
    private int getCompetitionCareCount(Long competitionId) {
        return careMapper.countByCompetitionId(competitionId);
    }
    
    private List<Competition> getCompetitionsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return competitionMapper.getByIds(ids);
    }
    
    private List<Competition> getLatestCompetitions(int limit) {
        return competitionMapper.getLatestCompetitions(limit);
    }
}