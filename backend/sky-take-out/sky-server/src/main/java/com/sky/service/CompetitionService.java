package com.sky.service;


import com.sky.entity.Competition;
import com.sky.vo.CompetitionVO;

public interface CompetitionService {
    public CompetitionVO getById(Long competitionId);

    void delete(Long competitionId);

    void save(Competition competition);
    
    void update(Competition competition);
}
