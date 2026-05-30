package com.sky.service;

import com.sky.entity.Competition;
import java.util.List;

public interface HomeService {
    
    List<Competition> getHotCompetitions(int limit);
    
    List<Competition> getRecommendedCompetitions(Long userId, int limit);
    
    void recordUserView(Long userId, Long competitionId);
}